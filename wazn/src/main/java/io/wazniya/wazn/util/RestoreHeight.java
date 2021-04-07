/*
 * Copyright (c) 2018 m2049r
 * Further modifications copyright (c) 2019 by WooKey.IO
 * Further developement copyright (c) 2020-2021 Project WAZN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.wazniya.wazn.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RestoreHeight {
    static private RestoreHeight Singleton = null;

    static public RestoreHeight getInstance() {
        if (Singleton == null) {
            synchronized (RestoreHeight.class) {
                if (Singleton == null) {
                    Singleton = new RestoreHeight();
                }
            }
        }
        return Singleton;
    }

    private Map<String, Long> blockheight = new HashMap<>();

    RestoreHeight() {
        blockheight.put("2021-04-01", 10000L);
    }

    public long getHeight(String date) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));
        parser.setLenient(false);
        try {
            return getHeight(parser.parse(date));
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public long getHeight(final Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.DST_OFFSET, 0);
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -3); // give it some leeway
        if (cal.get(Calendar.YEAR) < 2021)
            return 0;
        if ((cal.get(Calendar.YEAR) == 2021) && (cal.get(Calendar.MONTH) <= 2))
            // before May 2014
            return 0;

        Calendar query = (Calendar) cal.clone();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        String queryDate = formatter.format(date);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        long prevTime = cal.getTimeInMillis();
        String prevDate = formatter.format(prevTime);
        // lookup blockheight at first of the month
        Long prevBc = blockheight.get(prevDate);
        if (prevBc == null) {
            // if too recent, go back in time and find latest one we have
            while (prevBc == null) {
                cal.add(Calendar.MONTH, -1);
                if (cal.get(Calendar.YEAR) < 2021) {
                    throw new IllegalStateException("endless loop looking for blockheight");
                }
                prevTime = cal.getTimeInMillis();
                prevDate = formatter.format(prevTime);
                prevBc = blockheight.get(prevDate);
            }
        }
        long height = prevBc;
        // now we have a blockheight & a date ON or BEFORE the restore date requested
        if (queryDate.equals(prevDate)) return height;
        // see if we have a blockheight after this date
        cal.add(Calendar.MONTH, 1);
        long nextTime = cal.getTimeInMillis();
        String nextDate = formatter.format(nextTime);
        Long nextBc = blockheight.get(nextDate);
        if (nextBc != null) { // we have a range - interpolate the blockheight we are looking for
            long diff = nextBc - prevBc;
            long diffDays = TimeUnit.DAYS.convert(nextTime - prevTime, TimeUnit.MILLISECONDS);
            long days = TimeUnit.DAYS.convert(query.getTimeInMillis() - prevTime,
                    TimeUnit.MILLISECONDS);
            height = Math.round(prevBc + diff * (1.0 * days / diffDays));
        } else {
            long days = TimeUnit.DAYS.convert(query.getTimeInMillis() - prevTime,
                    TimeUnit.MILLISECONDS);
            height = Math.round(prevBc + 1.0 * days * (24 * 60 / 2));
        }
        return height;
    }
}
