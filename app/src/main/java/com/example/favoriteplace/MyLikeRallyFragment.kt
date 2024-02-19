package com.example.favoriteplace

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favoriteplace.databinding.FragmentMyLikeRallyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLikeRallyFragment : Fragment() {

    lateinit var binding: FragmentMyLikeRallyBinding
    private var myLikeRallyData= listOf<MyRally>()
    private var userToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMyLikeRallyBinding.inflate(inflater,container,false)


        checkLoginStatus(requireActivity()) // 인증 정보 불러오기

        getMyRally(requireActivity()) // 렐리 목록 불러오기

        return binding.root
    }

    fun getMyRally(context: FragmentActivity) {
        //내 프로필 정보 불러오기
        RetrofitAPI.myService.getMyLikeRallys("Bearer $userToken").enqueue(object: Callback<List<MyRally>> {
            override fun onResponse(call: Call<List<MyRally>>, response: Response<List<MyRally>>) {
                if(response.isSuccessful) {
                    val responseData = response.body()
                    if(responseData != null) {
                        Log.d("getMyRallys()", "Response: ${responseData}")

                        myLikeRallyData = responseData

                        val myLikeRallyRVAdapter=MyLikeRallyRVAdapter(context, myLikeRallyData)
                        binding.myLikeRallyRv.adapter=myLikeRallyRVAdapter
                        binding.myLikeRallyRv.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                    }
                }
                else {
                    Log.e("getMyRallys()", "notSuccessful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MyRally>>, t: Throwable) {
                Log.e("getMyRallys()", "onFailure: $t")
            }

        })
    }

    fun checkLoginStatus(context: FragmentActivity) {
        // SharedPreferences에서 액세스 토큰 가져오기
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString(LoginActivity.ACCESS_TOKEN_KEY, "") ?: ""

        if (userToken.isNotEmpty()) {
            Log.d("MyFragment", ">> 로그인 상태입니다.")
        }else{
            // 비회원 상태인 경우
            Log.d("MyFragment", ">> 비회원 상태입니다.")
        }
    }
}