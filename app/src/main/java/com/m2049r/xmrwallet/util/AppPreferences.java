/*
 * Copyright (c) 2018 Loki Project
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

package com.m2049r.xmrwallet.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Set;

public class AppPreferences {
    private static final String PREFERENCES_NAME = "loki_wallet_prefs";
    private static final String PREF_WALLETS_SET = "wallets_set";
    private static final String PREF_BALANCE_HIDDEN = "balance_hidden";

    public static void setBalanceHidden(Context context, boolean hidden) {
        getEditor(context).putBoolean(PREF_BALANCE_HIDDEN, hidden).apply();
    }

    public static boolean getBalanceHidden(Context context) {
        return getPrefs(context).getBoolean(PREF_BALANCE_HIDDEN, false);
    }

    public static Set<String> getWallets(Context context) {
        return getPrefs(context).getStringSet(PREF_WALLETS_SET, Collections.emptySet());
    }

    public static void setWallets(Context context, Set<String> wallets) {
        getEditor(context).putStringSet(PREF_WALLETS_SET, wallets).apply();
    }

    public static void removeWalletUserPass(Context context, String wallet) {
        getEditor(context).remove(wallet).apply();
    }

    public static void setWalletUserPass(Context context, String wallet, String value) {
        getEditor(context).putString(wallet, value).apply();
    }

    public static String getWalletUserPass(Context context, String wallet) {
        return getPrefs(context).getString(wallet, "");
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

}
