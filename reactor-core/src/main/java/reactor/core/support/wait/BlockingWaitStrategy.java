/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.support.wait;

import reactor.core.error.AlertException;
import reactor.fn.Consumer;
import reactor.fn.LongSupplier;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Blocking strategy that uses a lock and condition variable for ringbuffer consumer waiting on a barrier.
 *
 * This strategy can be used when throughput and low-latency are not as important as CPU resource.
 */
public final class BlockingWaitStrategy implements WaitStrategy
{
    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();

    @Override
    public long waitFor(long sequence, LongSupplier cursorSequence, Consumer<Void> barrier)
        throws AlertException, InterruptedException
    {
        long availableSequence;
        if ((availableSequence = cursorSequence.get()) < sequence)
        {
            lock.lock();
            try
            {
                while ((availableSequence = cursorSequence.get()) < sequence)
                {
                    barrier.accept(null);
                    processorNotifyCondition.await();
                }
            }
            finally
            {
                lock.unlock();
            }
        }

        while ((availableSequence = cursorSequence.get()) < sequence)
        {
            barrier.accept(null);
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking()
    {
        lock.lock();
        try
        {
            processorNotifyCondition.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }
}
