package cool.et.framework.id.core;

import cool.et.framework.id.contract.IdGeneratorOptions;
import cool.et.framework.id.contract.IdGeneratorException;

public class SnowWorkerM2 extends SnowWorkerM1 {

    public SnowWorkerM2(IdGeneratorOptions options) {
        super(options);
    }

    @Override
    public long nextId() {
        synchronized (_SyncLock) {
            long currentTimeTick = GetCurrentTimeTick();

            if (currentTimeTick < _LastTimeTick) {
                throw new IdGeneratorException("Clock moved backwards by %d milliseconds", _LastTimeTick - currentTimeTick);
            }

            if (_LastTimeTick == currentTimeTick) {
                if (++_CurrentSeqNumber > MaxSeqNumber) {
                    _CurrentSeqNumber = MinSeqNumber;
                    currentTimeTick = GetNextTimeTick();
                }
            } else {
                _CurrentSeqNumber = MinSeqNumber;
            }

            _LastTimeTick = currentTimeTick;
            long result = ((currentTimeTick << _TimestampShift) + ((long) WorkerId << SeqBitLength) + (int) _CurrentSeqNumber);

            return result;
        }

    }
}
