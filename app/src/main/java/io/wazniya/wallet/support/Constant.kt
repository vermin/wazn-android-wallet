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

const val REQUEST_CODE_PERMISSION_CAMERA = 501

const val ZH_CN = "zh-CN"
const val EN = "en"

val coinList = listOf(
        Coin("WAZN", "Wazn")
)

val nodeArray = arrayOf(
        Node().apply {
            symbol = "WAZN"
            url = "155.138.135.129:11787"
            isSelected = true
        },
        Node().apply {
            symbol = "WAZN"
            url = "45.76.193.160:11787"
            isSelected = false
        }
)
