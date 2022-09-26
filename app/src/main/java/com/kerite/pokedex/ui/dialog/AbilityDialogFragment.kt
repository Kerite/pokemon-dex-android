package com.kerite.pokedex.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kerite.pokedex.database.PokemonDatabase
import com.kerite.pokedex.databinding.FragmentDialogAbilityBinding
import com.kerite.pokedex.recyclers.AbilityDialogRecyclerAdapter
import com.kerite.pokedex.ui.BaseBottomDialogFragment
import com.kerite.pokedex.ui.activity.PokeDexDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AbilityDialogFragment(
        private val abilityName: String
) : BaseBottomDialogFragment<FragmentDialogAbilityBinding>(
        FragmentDialogAbilityBinding::inflate, false
) {
    private lateinit var recyclerAdapter: AbilityDialogRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d(abilityName)
        binding.apply {
            pokemonList.apply {
                recyclerAdapter = AbilityDialogRecyclerAdapter(context) {
                    val intent = Intent(context, PokeDexDetailsActivity::class.java)
                    intent.putExtra(PokeDexDetailsActivity.INTENT_DEX_NUMBER, it.dexNumber)
                    intent.putExtra(PokeDexDetailsActivity.INTENT_FORM_NAME, it.formName)
                    startActivity(intent)
                }
                this.adapter = recyclerAdapter
                layoutManager = LinearLayoutManager(context)
                lifecycleScope.launchWhenResumed {
                    val db = PokemonDatabase.getInstance(requireContext())
                    val detailsList = withContext(Dispatchers.IO) {
                        db.pokemonDetailsDao()
                                .filterByAbility(abilityName)
                    }
                    recyclerAdapter.submitList(detailsList)
                }
            }
            lifecycleScope.launchWhenResumed {
                val db = PokemonDatabase.getInstance(requireContext())
                val abilityInfo = withContext(Dispatchers.IO) {
                    db.pokemonAbilitySummary()
                            .findByAbilityName(abilityName)
                }
                abilityNameTextView.text = abilityInfo?.name
                abilityDescriptionTextView.text = abilityInfo?.description
            }
        }
    }
}