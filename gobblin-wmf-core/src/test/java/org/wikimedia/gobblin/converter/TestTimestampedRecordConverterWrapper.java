/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wikimedia.gobblin.converter;


import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import org.apache.gobblin.configuration.WorkUnitState;
import org.apache.gobblin.converter.DataConversionException;
import org.wikimedia.gobblin.TimestampedRecord;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

public class TestTimestampedRecordConverterWrapper {

    @Test(expectedExceptions=RuntimeException.class)
    public void testNoConfig() {
        WorkUnitState workUnitState = new WorkUnitState();

        TimestampedRecordConverterWrapper converter =
                (TimestampedRecordConverterWrapper) new TimestampedRecordConverterWrapper().init(workUnitState);
    }

    @Test(expectedExceptions=RuntimeException.class)
    public void testWrongConfig() {
        WorkUnitState workUnitState = new WorkUnitState();
        workUnitState.setProp(
                TimestampedRecordConverterWrapper.CONVERTER_TIMESTAMPED_RECORD_WRAPPED,
                "WrongClass");

        TimestampedRecordConverterWrapper converter =
                (TimestampedRecordConverterWrapper) new TimestampedRecordConverterWrapper().init(workUnitState);
    }

    @Test
    public void testConvertRecord() throws DataConversionException {
        WorkUnitState workUnitState = new WorkUnitState();
        workUnitState.setProp(
                TimestampedRecordConverterWrapper.CONVERTER_TIMESTAMPED_RECORD_WRAPPED,
                "org.apache.gobblin.converter.string.StringToBytesConverter");

        TimestampedRecordConverterWrapper converter =
                (TimestampedRecordConverterWrapper) new TimestampedRecordConverterWrapper().init(workUnitState);
        TimestampedRecord<String> input = new TimestampedRecord("test", Optional.absent());
        Iterator<TimestampedRecord<byte[]>> iterator = converter.convertRecord(null, input, new WorkUnitState()).iterator();
        Assert.assertTrue(iterator.hasNext());
        TimestampedRecord<byte[]> output = iterator.next();
        Assert.assertEquals(input.getTimestamp(), output.getTimestamp());
        Assert.assertEquals(input.getPayload().getBytes(Charsets.UTF_8), output.getPayload());
        Assert.assertFalse(iterator.hasNext());
    }
}