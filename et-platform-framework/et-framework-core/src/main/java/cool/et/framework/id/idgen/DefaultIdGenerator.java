package cool.et.framework.id.idgen;

import cool.et.framework.id.contract.IIdGenerator;
import cool.et.framework.id.contract.ISnowWorker;
import cool.et.framework.id.contract.IdGeneratorException;
import cool.et.framework.id.contract.IdGeneratorOptions;
import cool.et.framework.id.core.SnowWorkerM1;
import cool.et.framework.id.core.SnowWorkerM2;

public class DefaultIdGenerator implements IIdGenerator {

    private final ISnowWorker worker;

    public DefaultIdGenerator(IdGeneratorOptions options) throws IdGeneratorException {
        if (options == null) {
            throw new IdGeneratorException("options error.");
        }

        // 1.BaseTime
        if (options.BaseTime < 315504000000L || options.BaseTime > System.currentTimeMillis()) {
            throw new IdGeneratorException("BaseTime error.");
        }

        // 2.WorkerIdBitLength
        if (options.WorkerIdBitLength <= 0) {
            throw new IdGeneratorException("WorkerIdBitLength error.(range:[1, 21])");
        }
        if (options.WorkerIdBitLength + options.SeqBitLength > 22) {
            throw new IdGeneratorException("error：WorkerIdBitLength + SeqBitLength <= 22");
        }

        // 3.WorkerId
        int maxWorkerIdNumber = (1 << options.WorkerIdBitLength) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.WorkerId < 0 || options.WorkerId > maxWorkerIdNumber) {
            throw new IdGeneratorException(
                    "WorkerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // 4.SeqBitLength
        if (options.SeqBitLength < 2 || options.SeqBitLength > 21) {
            throw new IdGeneratorException("SeqBitLength error. (range:[2, 21])");
        }

        // 5.MaxSeqNumber
        int maxSeqNumber = (1 << options.SeqBitLength) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.MaxSeqNumber < 0 || options.MaxSeqNumber > maxSeqNumber) {
            throw new IdGeneratorException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // 6.MinSeqNumber
        if (options.MinSeqNumber < 5 || options.MinSeqNumber > (options.MaxSeqNumber == 0 ? maxSeqNumber : options.MaxSeqNumber)) {
            throw new IdGeneratorException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        // 7.TopOverCostCount
        if (options.TopOverCostCount < 0 || options.TopOverCostCount > 10000) {
            throw new IdGeneratorException("TopOverCostCount error. (range:[0, 10000]");
        }

        switch (options.Method) {
            case 2:
                worker = new SnowWorkerM2(options);
                break;
            case 1:
            default:
                worker = new SnowWorkerM1(options);
                break;
        }

        if (options.Method == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IdGeneratorException("Interrupted while initializing the ID generator", e);
            }
        }
    }

    @Override
    public long newLong() {
        return worker.nextId();
    }
}
