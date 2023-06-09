package de.softdeveloper.shoppinglistroom

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.softdeveloper.shoppinglistroom.adapter.ShoppingMemoListAdapter
import de.softdeveloper.shoppinglistroom.database.ShoppingMemo
import de.softdeveloper.shoppinglistroom.databinding.ActivityMainBinding
import de.softdeveloper.shoppinglistroom.databinding.DialogEditShoppingMemoBinding
import de.softdeveloper.shoppinglistroom.viewmodel.ShoppingMemoViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogEditShoppingMemoBinding: DialogEditShoppingMemoBinding
    private lateinit var shoppingMemoViewModel: ShoppingMemoViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val adapter = ShoppingMemoListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        dialogEditShoppingMemoBinding = DialogEditShoppingMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvShoppingMemos.layoutManager = LinearLayoutManager(this)
        binding.rvShoppingMemos.adapter = adapter

        shoppingMemoViewModel = ShoppingMemoViewModel(application)
        shoppingMemoViewModel.getAllShoppingMemos()?.observe(this) {
            adapter.setShoppingMemos(it)
        }

        adapter.setOnItemClickListener(object : ShoppingMemoListAdapter.OnItemClickListener {
            override fun onItemClick(memo: ShoppingMemo) {
                memo.isSelected = !memo.isSelected
                shoppingMemoViewModel.insertOrUpdate(memo)
            }
        })

        binding.btnAddProduct.setOnClickListener {
            if (TextUtils.isEmpty(binding.etQuantity.text)) {
                binding.etQuantity.error = "Feld darf nicht leer sein"
                binding.etQuantity.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(binding.etProduct.text)) {
                binding.etProduct.error = "Feld darf nicht leer sein"
                binding.etProduct.requestFocus()
                return@setOnClickListener
            }
            shoppingMemoViewModel.insertOrUpdate(
                ShoppingMemo(
                    binding.etQuantity.text.toString().toInt(),
                    binding.etProduct.text.toString()
                )
            )
            binding.etProduct.text.clear()
            binding.etQuantity.text.clear()
            binding.etQuantity.requestFocus()
        }

        binding.etProduct.setOnEditorActionListener { _, _, _ ->
            binding.btnAddProduct.performClick()
        }

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val currentMemo =
                    shoppingMemoViewModel.getAllShoppingMemos()?.value?.get(viewHolder.adapterPosition)
                when(direction){
                    ItemTouchHelper.RIGHT ->{ // Löschen eines Eintrags
                        shoppingMemoViewModel.delete(currentMemo!!)
                        Snackbar.make(this@MainActivity,binding.root,"Löschen rückgangig machen",Snackbar.LENGTH_LONG)
                            .setAction("UNDO", object : OnClickListener {
                                override fun onClick(v: View?) {
                                    shoppingMemoViewModel.insertOrUpdate(currentMemo)
                                }
                            }).show()
                    }
                    ItemTouchHelper.LEFT ->{ // Editieren eines Eintrags
                        val builder = AlertDialog.Builder(this@MainActivity)
                        dialogEditShoppingMemoBinding.etEditProduct.setText(currentMemo?.product)
                        dialogEditShoppingMemoBinding.etEditQuantity.setText(currentMemo?.quantity.toString())
                        val dialogView = dialogEditShoppingMemoBinding.root
                        if(dialogView.parent != null){
                            (dialogView.parent as ViewGroup).removeView(dialogView)
                        }
                        builder.setView(dialogView)
                            .setTitle("Eintrag ändern")
                            .setPositiveButton("Ändern"){dialog,wich ->
                                currentMemo?.quantity = dialogEditShoppingMemoBinding.etEditQuantity.text.toString().toInt()
                                currentMemo?.product = dialogEditShoppingMemoBinding.etEditProduct.text.toString()
                                shoppingMemoViewModel.insertOrUpdate(currentMemo!!)
                                dialog.dismiss()
                            }
                            .setNegativeButton("Abbrechen"){dialog,wich ->
                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                                dialog.cancel()
                            }
                            .create().show()
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(Color.LTGRAY)
                    .addSwipeRightActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeLeftActionIcon(R.drawable.ic_edit_black_24dp)
                    .create()
                    .decorate()

                // Version ohne SwipeDecorator
//                val itemView = viewHolder.itemView
//                val background = ColorDrawable(Color.LTGRAY)
//                val iconDelete = getDrawable(R.drawable.ic_delete_black_24dp)
//                val iconEdit = getDrawable(R.drawable.ic_edit_black_24dp)
//                val backgroundCornerOffset = 20
//                val iconMargin = (itemView.height - iconEdit!!.intrinsicHeight) / 2
//                val iconTop =
//                    itemView.top + (itemView.height - iconEdit.intrinsicHeight) / 2
//                val iconBottom = iconTop + iconEdit.intrinsicHeight
//                if (dX > 0) { // Swiping to the right
//                    val iconLeft =
//                        itemView.left + iconMargin + iconDelete!!.intrinsicWidth
//                    val iconRight = itemView.left + iconMargin
//                    iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                    background.setBounds(
//                        itemView.left,
//                        itemView.top,
//                        itemView.left + dX.toInt() + backgroundCornerOffset,
//                        itemView.bottom
//                    )
//                } else if (dX < 0) { // Swiping to the left
//                    val iconLeft =
//                        itemView.right - iconMargin - iconEdit!!.intrinsicWidth
//                    val iconRight = itemView.right - iconMargin
//                    iconEdit.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//                    background.setBounds(
//                        itemView.right + dX.toInt() - backgroundCornerOffset,
//                        itemView.top, itemView.right, itemView.bottom
//                    )
//                } else { // view is unSwiped
//                    background.setBounds(0, 0, 0, 0)
//                }
//                background.draw(c)
//                iconDelete!!.draw(c)
//                iconEdit!!.draw(c)
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rvShoppingMemos)

    }
}