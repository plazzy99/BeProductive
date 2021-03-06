package com.vatsal.kesarwani.login.ui.fragment.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.vatsal.kesarwani.core.extensions.showToast
import com.vatsal.kesarwani.core.loadingDialog.ViewDialog
import com.vatsal.kesarwani.core.utils.SharedPrefUtil
import com.vatsal.kesarwani.core.viewmodelfactory.ViewModelProviderFactory
import com.vatsal.kesarwani.login.data.response.LoginResponse
import com.vatsal.kesarwani.login.databinding.FragmentLoginBinding
import com.vatsal.kesarwani.login.ui.AuthViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import com.vatsal.kesarwani.login.ui.fragment.login.LoginViewState.*
import com.vatsal.kesarwani.login.utils.VerifyType
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil
    
    @Inject
    lateinit var viewModelFactory : ViewModelProviderFactory

    lateinit var viewModel : LoginViewModel

    private val activityViewModel: AuthViewModel by activityViewModels()

    private lateinit var viewBinding : FragmentLoginBinding

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var loadingDialog : ViewDialog

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentLoginBinding.inflate(inflater)
        initUI()
        observeRendering()
        fieldObserve()
        return viewBinding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        compositeDisposable = CompositeDisposable()
        loadingDialog = ViewDialog(requireContext())
    }

    private fun initUI() {
        viewBinding.viewModel = viewModel
        viewBinding.lifecycleOwner = viewLifecycleOwner
    }

    private fun fieldObserve() {
        viewModel.email.observe(viewLifecycleOwner , {
            viewBinding.etEmail.error = null
        })
    }

    private fun observeRendering() {
        viewModel.stateObservable.observe(this, {
            render(it)
        })
    }

    private fun render(state : LoginViewState) {
        when(state) {
            Loading -> showLoading()
            DoLogin -> doLogin()
            is OnSuccess -> onSuccess(state.loginResponse)
            is OnError -> showToast(state.mssg)
            is OnFieldError -> viewBinding.etEmail.error = state.mssg
        }
    }

    private fun doLogin(){
        addDisposable(
                viewModel.login()
        )
    }

    private fun showLoading() {
        loadingDialog.showDialog()
    }

    private fun hideLoading() {
        loadingDialog.hideDialog()
    }

    private fun onSuccess(loginResponse: LoginResponse) {
        hideLoading()
        when(loginResponse.data){
            "OTP SENT" -> {
                sharedPrefUtil.loginEmail = viewBinding.etEmail.text.toString()
                activityViewModel.goToOtpScreen("",VerifyType.EMAIL)
            }
            else -> showToast("something went wrong")
        }

    }

    override fun onStop() {
        super.onStop()
        hideLoading()
    }

    private fun addDisposable(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.add(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}