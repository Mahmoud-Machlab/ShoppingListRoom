package de.softdeveloper.shoppinglistroom.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.softdeveloper.shoppinglistroom.database.ShoppingMemo
import de.softdeveloper.shoppinglistroom.repository.ShoppingMemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ShoppingMemoViewModel: ViewModel() {

    private var repository:ShoppingMemoRepository? = null
    private var allShoppingMemos: LiveData<List<ShoppingMemo>>? = null

    operator fun invoke(app: Application):ShoppingMemoViewModel{
        repository = ShoppingMemoRepository(app)
        allShoppingMemos = repository?.getAllShoppingMemos()
        return this
    }

    fun getAllShoppingMemos():LiveData<List<ShoppingMemo>>?{
        return allShoppingMemos
    }

    fun insertOrUpdate(memo: ShoppingMemo){
        CoroutineScope(Dispatchers.IO).launch {
            repository?.insertOrUpdate(memo)
        }
    }

    fun delete(memo: ShoppingMemo){
        CoroutineScope(Dispatchers.IO).launch {
            repository?.delete(memo)
        }
    }

}