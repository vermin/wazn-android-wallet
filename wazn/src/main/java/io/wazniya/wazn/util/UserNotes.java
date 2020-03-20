/*
 * Copyright (c) 2017 m2049r
 * Further modifications copyright (c) 2019 by WooKey.IO
 * Further developement copyright (c) 2020 Project WAZN
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


import io.wazniya.wazn.api.QueryOrderStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserNotes {
    public String txNotes = "";
    public String note = "";
    public String wazntoKey = null;
    public String wazntoAmount = null; // could be a double - but we are not doing any calculations
    public String wazntoDestination = null;

    public UserNotes(final String txNotes) {
        if (txNotes == null) {
            return;
        }
        this.txNotes = txNotes;
        Pattern p = Pattern.compile("^\\{(waznto-\\w{6}),([0-9.]*)BTC,(\\w*)\\} ?(.*)");
        Matcher m = p.matcher(txNotes);
        if (m.find()) {
            wazntoKey = m.group(1);
            wazntoAmount = m.group(2);
            wazntoDestination = m.group(3);
            note = m.group(4);
        } else {
            note = txNotes;
        }
    }

    public void setNote(String newNote) {
        if (newNote != null) {
            note = newNote;
        } else {
            note = "";
        }
        txNotes = buildTxNote();
    }

    public void setWAZNtoStatus(QueryOrderStatus wazntoStatus) {
        if (wazntoStatus != null) {
            wazntoKey = wazntoStatus.getUuid();
            wazntoAmount = String.valueOf(wazntoStatus.getBtcAmount());
            wazntoDestination = wazntoStatus.getBtcDestAddress();
        } else {
            wazntoKey = null;
            wazntoAmount = null;
            wazntoDestination = null;
        }
        txNotes = buildTxNote();
    }

    private String buildTxNote() {
        StringBuffer sb = new StringBuffer();
        if (wazntoKey != null) {
            if ((wazntoAmount == null) || (wazntoDestination == null))
                throw new IllegalArgumentException("Broken notes");
            sb.append("{");
            sb.append(wazntoKey);
            sb.append(",");
            sb.append(wazntoAmount);
            sb.append("BTC,");
            sb.append(wazntoDestination);
            sb.append("}");
            if ((note != null) && (!note.isEmpty()))
                sb.append(" ");
        }
        sb.append(note);
        return sb.toString();
    }
}
