package io.wazniya.wallet.support

import io.wazniya.wallet.data.entity.Coin
import io.wazniya.wallet.data.entity.Node

const val WALLET_RECOVERY = 1
const val WALLET_CREATE = 0

const val TRANSFER_ALL = 0
const val TRANSFER_IN = 1
const val TRANSFER_OUT = 2

const val SELECT_ADDRESS = 1

const val REQUEST_SCAN_ADDRESS = 100
const val REQUEST_SELECT_COIN = 101
const val REQUEST_SELECT_ADDRESS = 102
const val REQUEST_SELECT_NODE = 103
const val REQUEST_SELECT_SUB_ADDRESS = 104

const val REQUEST_SWAP_SCAN_ADDRESS = 200
const val REQUEST_SWAP_SELECT_ADDRESS = 202

const val REQUEST_PATTERN_SETTING = 105
const val REQUEST_PATTERN_CHECKING = 106
const val REQUEST_PATTERN_CHECKING_ADDRESS_SETTING = 107
const val REQUEST_PATTERN_CHECKING_BACKUP_MNEMONIC = 108
const val REQUEST_PATTERN_CHECKING_BACKUP_KEY = 109

const val REQUEST_CODE_PERMISSION_CAMERA = 501

const val ZH_CN = "zh-CN"
const val EN = "en"

const val KEY_ALIAS = "wazniyaWazn"
const val RSA_KEY_ALIAS = "wazniyaWaznRSA"

val coinList = listOf(
    Coin("WAZN", "Wazn")
)

val nodeArray = arrayOf(
        Node().apply {
        symbol = "WAZN"
        url = "78.47.140.87:11787"
            isSelected = true
        },
        Node().apply {
        symbol = "WAZN"
        url = "95.217.185.212:11787"
            isSelected = false
        },
        Node().apply {
        symbol = "WAZN"
        url = "168.119.189.236:11787"
            isSelected = false
    }
)
