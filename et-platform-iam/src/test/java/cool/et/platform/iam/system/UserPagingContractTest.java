package cool.et.platform.iam.system;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.et.platform.iam.contract.user.UserInfoResp;
import cool.et.platform.iam.contract.user.UserPage;
import cool.et.platform.iam.contract.user.UserPageReq;
import cool.et.platform.iam.feign.UserFeign;
import cool.et.platform.iam.system.controller.UserController;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.domain.enums.Sex;
import cool.et.platform.iam.system.repository.UserMapper;
import cool.et.platform.iam.system.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockMakers;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserPagingContractTest {
    private final UserService users = mock(UserService.class, withSettings().mockMaker(MockMakers.PROXY));
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new UserController(users, null)).build();
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void feignAndControllerShareFiltersAndSortOptions() throws Exception {
        var method = UserFeign.class.getMethod("pageList", UserPageReq.class);
        var metadata = new SpringMvcContract().parseAndValidateMetadata(UserFeign.class, method);
        assertThat(metadata.bodyType()).isEqualTo(UserPageReq.class);

        var query = UserPageReq.builder().username("member").nickname("Member").email("member@example.test")
                .sex("2").orgId(12L).mobile("13900000000").status(false)
                .current(3).size(20).column("createTime").asc(false).build();
        var user = User.builder().id(42L).username("member").sex(Sex.WOMAN).build();
        when(users.pageList(query)).thenReturn(new Page<User>(3, 20, 41).setRecords(List.of(user)));
        var template = metadata.template();
        var response = mvc.perform(request(HttpMethod.valueOf(template.method()), template.url())
                        .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(query)))
                .andExpect(status().isOk()).andReturn().getResponse();
        var page = json.readValue(response.getContentAsByteArray(), UserPage.class);
        assertThat(page.getRecords()).singleElement().satisfies(row -> {
            assertThat(row.getId()).isEqualTo(42L);
            assertThat(row.getSex()).isEqualTo("2");
        });
        var previousPage = new Page<UserInfoResp>(3, 20, 41).setRecords(page.getRecords());
        assertThat(json.readTree(response.getContentAsByteArray())).isEqualTo(json.readTree(json.writeValueAsBytes(previousPage)));
        verify(users).pageList(query);
    }

    @Test
    void omittedPagingFieldsUseTheIamDefaults() throws Exception {
        when(users.pageList(any())).thenReturn(new Page<>());
        mvc.perform(post("/users/page").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());
        verify(users).pageList(UserPageReq.builder().current(1).size(10).asc(true).build());
        assertThat(UserPageReq.builder().build()).isEqualTo(new UserPageReq());
    }

    @Test
    void lookupRoutesReturnTheSameVisibleUserAndPreserveBatchIds() throws Exception {
        var user = User.builder().id(42L).username("member").tenantId(7L).readonly(true).sex(Sex.MAN)
                .password("internal").build();
        when(users.requireVisibleUser(42L)).thenReturn(user);
        when(users.visibleUsersByIds(Set.of(42L, 99L))).thenReturn(List.of(user));

        var contract = new SpringMvcContract();
        var single = contract.parseAndValidateMetadata(UserFeign.class, UserFeign.class.getMethod("findById", Long.class));
        var detail = mvc.perform(request(HttpMethod.valueOf(single.template().method()), single.template().url(), 42))
                .andExpect(status().isOk()).andReturn().getResponse();
        var row = json.readTree(detail.getContentAsByteArray());
        assertThat(row.path("tenantId").asLong()).isEqualTo(7);
        assertThat(row.path("readonly").asBoolean()).isTrue();
        assertThat(row.path("sex").asString()).isEqualTo("1");
        assertThat(row.has("password")).isFalse();

        var batch = contract.parseAndValidateMetadata(UserFeign.class, UserFeign.class.getMethod("findByIds", Set.class));
        var response = mvc.perform(request(HttpMethod.valueOf(batch.template().method()), batch.template().url())
                        .contentType(MediaType.APPLICATION_JSON).content("[42,99]"))
                .andExpect(status().isOk()).andReturn().getResponse();
        var usersById = json.readTree(response.getContentAsByteArray());
        assertThat(usersById).isEqualTo(json.valueToTree(Map.of(42L, row)));
    }

    @Test
    void mapperUsesRequestedPageAndConvertsSortFieldToDatabaseColumn() {
        var query = UserPageReq.builder().current(3).size(20).column("createTime").asc(false).build();
        var filters = Wrappers.<User>lambdaQuery();
        var mapper = mock(UserMapper.class, withSettings().mockMaker(MockMakers.PROXY));
        doCallRealMethod().when(mapper).selectUserPage(query, filters);
        doAnswer(call -> call.getArgument(0)).when(mapper).selectPage(any(), same(filters));

        var page = mapper.selectUserPage(query, filters);
        assertThat(page.getCurrent()).isEqualTo(3);
        assertThat(page.getSize()).isEqualTo(20);
        assertThat(page.orders()).singleElement().satisfies(order -> {
            assertThat(order.getColumn()).isEqualTo("create_time");
            assertThat(order.isAsc()).isFalse();
        });
    }
}
