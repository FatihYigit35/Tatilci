package com.androiddevelopers.villabuluyorum.view.login

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentRegisterBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.login.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var verificationDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(requireContext()).create()
        verificationDialog = AlertDialog.Builder(requireContext()).create()


        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassworad.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            viewModel.signUp(email,password,confirmPassword)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnPhone.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToPhoneLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
        setupDialogs()
        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                }
                Status.LOADING->{
                    binding.pbRegister.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                Status.SUCCESS->{
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.btnRegister.isEnabled = true
                }
            }
        })

        viewModel.registrationError.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    if (it.data == true){
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }else{
                        binding.etConfirmPassword.error = it.message
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.isVerificationEmailSent.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    verificationDialog?.show()
                }
                Status.ERROR -> {
                    errorDialog?.show()
                }
                else->{
                    errorDialog?.show()
                }
            }
        })
    }
    private fun setupDialogs() {
        verificationDialog?.setTitle("Email Doğrulama")
        verificationDialog?.setMessage("Doğrulama e-postası gönderildi. Lütfen e-posta kutunuzu kontrol edin.")
        verificationDialog?.setCancelable(false)

        verificationDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->
            findNavController().popBackStack()
        }

        errorDialog?.setTitle("Hata")
        errorDialog?.setMessage("e-posta gönderilemedi")
        errorDialog?.setCancelable(false)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tekrar gönder") { _, _ ->

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}