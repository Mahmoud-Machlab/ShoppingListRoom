package de.softdeveloper.shoppinglistroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import de.softdeveloper.shoppinglistroom.adapter.ShoppingMemoListAdapter
import de.softdeveloper.shoppinglistroom.database.ShoppingMemo
import de.softdeveloper.shoppinglistroom.databinding.ActivityMainBinding
import de.softdeveloper.shoppinglistroom.viewmodel.ShoppingMemoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var shoppingMemoViewModel: ShoppingMemoViewModel
    private val adapter = ShoppingMemoListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvShoppingMemos.layoutManager = LinearLayoutManager(this)
        binding.rvShoppingMemos.adapter = adapter

        shoppingMemoViewModel = ShoppingMemoViewModel(application)
        shoppingMemoViewModel.getAllShoppingMemos()?.observe(this){
            adapter.setShoppingMemos(it)
        }

        adapter.setOnItemClickListener(object : ShoppingMemoListAdapter.OnItemClickListener {
            override fun onItemClick(memo: ShoppingMemo) {
                memo.isSelected = !memo.isSelected
                shoppingMemoViewModel.insertOrUpdate(memo)
            }
        })

        binding.btnAddProduct.setOnClickListener {
            if (TextUtils.isEmpty(binding.etQuantity.text)){
                binding.etQuantity.error= "Feld darf nicht leer sein"
                binding.etQuantity.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etProduct.text)){
                binding.etProduct.error= "Feld darf nicht leer sein"
                binding.etProduct.requestFocus()
                return@setOnClickListener
            }
            shoppingMemoViewModel.insertOrUpdate(
                ShoppingMemo(binding.etQuantity.text.toString().toInt(),binding.etProduct.text.toString())
            )
            binding.etProduct.text.clear()
            binding.etQuantity.text.clear()
            binding.etQuantity.requestFocus()
        }

        binding.etProduct.setOnEditorActionListener { _, _, _ ->
            binding.btnAddProduct.performClick()
        }


    }
}