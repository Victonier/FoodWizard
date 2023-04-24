package com.example.foodwizard.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.foodwizard.Meal
import com.example.foodwizard.databinding.ListItemMealBinding
import androidx.fragment.app.setFragmentResult

class MealHolder(
    val binding: ListItemMealBinding
) : RecyclerView.ViewHolder(binding.root) {

}

class mealAdapter(
    private val meals: List<Meal>,
    private val onMealClicked: () -> Unit
) : RecyclerView.Adapter<MealHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) : MealHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMealBinding.inflate(inflater, parent, false)
        return MealHolder(binding)
    }

    override fun onBindViewHolder(holder: MealHolder, position: Int) {
        if (position<meals.size)
        {
            val meal = meals[position]
            holder.apply {
                binding.food.visibility= View.INVISIBLE
                binding.upload.visibility=View.INVISIBLE
                binding.name.visibility=View.VISIBLE
                binding.time.visibility=View.VISIBLE
                binding.root.setOnClickListener(){
                   onMealClicked()
                }
            }

        }
        else
        {
            holder.apply{
                binding.upload.visibility=View.VISIBLE
                binding.root.setOnClickListener(){

                }
            }
        }

    }



    override fun getItemCount() = meals.size+1
}