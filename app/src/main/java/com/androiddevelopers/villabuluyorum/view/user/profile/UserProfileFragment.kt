package com.androiddevelopers.villabuluyorum.view.user.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentUserProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.UserProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: UserProfileViewModel by viewModels()

    private val villaAdapter: HouseAdapter = HouseAdapter()

    private lateinit var userId: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        userId = arguments?.getString("user_id") ?: ""
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        villaAdapter.inProfile = true
        if (userId.isNotEmpty()) {
            viewModel.getUserVillas(userId, 20)
            viewModel.getUserData(userId)
        }
        binding.rvUserVillas.adapter = villaAdapter
        observeLiveData()

    }

    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.layoutProfileError.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.VISIBLE
                }
            }
        })
        viewModel.userVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null && villas.isNotEmpty()) {
                villaAdapter.housesList = villas
                binding.layoutEmptyVillas.visibility = View.GONE
            } else {
                binding.layoutEmptyVillas.visibility = View.VISIBLE
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivProfilePhoto)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}