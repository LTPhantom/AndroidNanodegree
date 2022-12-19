package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.AsteroidsFilter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this,
            MainViewModel.Factory(requireActivity().application))[MainViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        val asteroidListAdapter = AsteroidListAdapter(
            AsteroidListAdapter.OnClickListener { asteroid: Asteroid ->
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })
        binding.asteroidRecycler.adapter = asteroidListAdapter
        viewModel.asteroids.observe(viewLifecycleOwner) { asteroids ->
            asteroids?.let {
                asteroidListAdapter.submitList(it)
            }
        }
        viewModel.nasaApiStatus.observe(viewLifecycleOwner) {
            if (it == NasaApiStatus.ERROR) {
                Toast.makeText(requireContext(), getString(R.string.fetch_error), Toast.LENGTH_LONG).show()
                viewModel.onErrorAcknowledged()
            }
        }


        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.show_week_menu -> {
                viewModel.reloadAsteroids(AsteroidsFilter.WEEK)
            }
            R.id.show_today_menu -> {
                viewModel.reloadAsteroids(AsteroidsFilter.TODAY)
            }
            R.id.show_saved_menu -> {
                viewModel.reloadAsteroids(AsteroidsFilter.SAVED)
            }
        }
        return true
    }
}
