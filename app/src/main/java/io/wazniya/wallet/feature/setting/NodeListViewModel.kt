package io.wazniya.wallet.feature.setting

import androidx.lifecycle.MutableLiveData
import io.wazniya.wallet.R
import io.wazniya.wallet.base.BaseViewModel
import io.wazniya.wallet.core.WAZNRepository
import io.wazniya.wallet.core.WAZNWalletController
import io.wazniya.wallet.data.AppDatabase
import io.wazniya.wallet.data.entity.Node
import io.wazniya.wallet.support.nodeArray
import io.wazniya.wallet.support.viewmodel.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NodeListViewModel : BaseViewModel() {

    val showConfirmDialog = MutableLiveData<String>()

    val showLoading = MutableLiveData<Boolean>()
    val hideLoading = MutableLiveData<Boolean>()
    val toast = MutableLiveData<String>()
    val toastRes = MutableLiveData<Int>()
    val dataChanged = MutableLiveData<Boolean>()

    val finish = SingleLiveEvent<Node>()

    private val repository = WAZNRepository()

    private var deleteNode: Node? = null
    private var canDelete = true

    init {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertNodes()
            }
        }
    }

    fun insertNode(node: Node) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance().nodeDao().insertNodes(node)
                dataChanged.postValue(true)
            }
        }
    }

    fun updateNode(nodes: List<Node>, node: Node) {
        uiScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val nodeDao = AppDatabase.getInstance().nodeDao()
                    val filter = nodes.filter {
                        it.isSelected
                    }
                    if (filter.isNullOrEmpty()) {
                        return@withContext
                    }
                    if (filter.size != 1) {
                        return@withContext
                    }
                    nodeDao.updateNodes(filter[0].apply { isSelected = false }, node.apply { isSelected = true })
                }
                finish.postValue(node)
            } catch (e: Exception) {
                toast.postValue(e.message)
            }
        }
    }

    fun onLongClick(node: Node) {

        if (!canDelete) return

        var isDefault = false
        nodeArray.forEach {
            if (it.symbol == node.symbol && it.url == node.url) {
                isDefault = true
                return@forEach
            }
        }
        if (isDefault) {
            toastRes.value = R.string.can_not_delete
        } else {
            deleteNode = node
            showConfirmDialog.value = node.url
        }
    }

    fun cancelDelete() {
        deleteNode = null
    }

    fun confirmDelete(symbol: String) {
        if (deleteNode == null) return
        showLoading.postValue(true)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance().nodeDao().deleteNode(deleteNode!!)
                if (deleteNode!!.isSelected) {
                    val nodes = AppDatabase.getInstance().nodeDao().getSymbolNodes(symbol)
                    if (!nodes.isNullOrEmpty()) {
                        AppDatabase.getInstance().nodeDao().updateNodes(nodes[0].apply { isSelected = true })
                    }
                }
            }
            hideLoading.postValue(true)
        }

    }

    fun testRpcService(nodes: List<Node>) {
        nodes.forEach {
            testRpcService(it)
        }
    }

    fun testRpcService(node: Node) {
        uiScope.launch {
            try {
                node.responseTime = withContext(Dispatchers.IO) {
                    WAZNWalletController.testRpcService(node.url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                node.responseTime = 5 * 1000
            }
            dataChanged.postValue(true)
        }
    }

    fun setCanDelete(canDelete: Boolean) {
        this.canDelete = canDelete
    }
}
