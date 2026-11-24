package cool.et.framework.db.mybatisplus.handler.type;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * List<String> 与数据库字符串互转
 * 支持逗号分隔格式：tool1,tool2,tool3
 */
@MappedTypes(value = {List.class})
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, StrUtil.join(StrUtil.COMMA, parameter));
    }

    @Override
    @SneakyThrows
    public List<String> getNullableResult(ResultSet rs, String columnName) {
        String reString = rs.getString(columnName);
        if (StrUtil.isBlank(reString)) {
            return null;
        }
        return Convert.toList(String.class, reString);
    }

    @Override
    @SneakyThrows
    public List<String> getNullableResult(ResultSet rs, int columnIndex) {
        String reString = rs.getString(columnIndex);
        if (StrUtil.isBlank(reString)) {
            return null;
        }
        return Convert.toList(String.class, reString);
    }

    @Override
    @SneakyThrows
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) {
        String reString = cs.getString(columnIndex);
        if (StrUtil.isBlank(reString)) {
            return null;
        }
        return Convert.toList(String.class, reString);
    }

}