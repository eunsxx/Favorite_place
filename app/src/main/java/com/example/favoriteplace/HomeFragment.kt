package com.example.favoriteplace


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.favoriteplace.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var retrofit: Retrofit
    private lateinit var homeService: HomeService
    private lateinit var trendingPostsAdapter: TrendingPostsAdapter // Adapter 선언


    private var trendingPostsData: MutableList<HomeService.TrendingPosts> = mutableListOf()
    private var isLoggedIn = false // 로그인 상태를 나타내는 변수

    companion object{
        const val LOGIN_REQUEST_CODE=101

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Retrofit 객체 생성
        retrofit = Retrofit.Builder()
            .baseUrl("http://favoriteplace.store:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        homeService = retrofit.create(HomeService::class.java)


        val bannerAdapter = BannerVPAdapter(this)
        binding.homeBannerVp.adapter=bannerAdapter
        binding.homeBannerVp.orientation=ViewPager2.ORIENTATION_HORIZONTAL
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_banner1))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_banner1))




        //로그인 버튼
        binding.homeLoginBtn.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            try {
                startActivityForResult(intent, LOGIN_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 앱이 처음 시작될 때 로그인 상태를 확인하고, 로그인 정보가 없으면 서버에 요청을 보냄
        checkLoginStatus()

    }

    override fun onStart() {
        super.onStart()
        //checkLoginStatus()
    }

    private fun checkLoginStatus() {
        // SharedPreferences에서 액세스 토큰 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // 로그인 상태인 경우 사용자 정보를 가져옴
            val userToken = sharedPreferences.getString("accessToken", "")
            if (!userToken.isNullOrEmpty()) {
                getUserInfo(userToken)
                Log.d("HomeFragment", ">> 로그인 상태인 경우 사용자 정보를 가져옴, $userToken")
            }
        }else{
            // 비회원 상태인 경우
            Log.d("HomeFragment", ">> 비회원 상태입니다., $isLoggedIn")

        }
    }


    private fun sendLoginStatusToServer(isLoggedIn: String?) {

        lifecycleScope.launch {
            try {
                // 로그인 상태를 서버에 전달
                val response: Response<HomeService.LoginResponse> = homeService.getUserInfo(isLoggedIn)
                if (response.isSuccessful) {
                    Log.d("HomeFragment", ">> Login status sent to server: $isLoggedIn")
                } else {
                    Log.e("HomeFragment", "Failed to send login status to server: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error sending login status to server: ${e.message}", e)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        // 앱이 종료될 때 로그아웃 상태를 SharedPreferences에 저장
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }

    }

    private fun setupTrendingPostsRecyclerView() {

        // TrendingPostsAdapter 초기화
        trendingPostsAdapter = TrendingPostsAdapter(trendingPostsData)
        binding.trendingPostsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.trendingPostsRecyclerView.adapter = trendingPostsAdapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            val userToken = data?.getStringExtra("accessToken")
            if (!userToken.isNullOrEmpty()) {
                Log.d("HomeFragment", ">> Home Login userToken : $userToken")
                // Retrofit 요청 -> 사용자 정보 가져옴
                getUserInfo(userToken)
            }
        }
    }

    private fun getUserInfo(userToken: String) {
        lifecycleScope.launch {
            try {
                val response: Response<HomeService.LoginResponse> = homeService.getUserInfo("Bearer $userToken")
                if(response.isSuccessful){
                    // 로그인 상태인 경우
                    // 서버로부터 사용자 정보를 성공적으로 받아왔을 때 UI 업데이트
                    val loginResponse: HomeService.LoginResponse? = response.body()
                    if(loginResponse != null){
                        updateUI(loginResponse)

                        Log.d("HomeFragment", ">> Home Login Success")
                        Log.d("HomeFragment", ">> $loginResponse")

                    }else{
                        updateUI(null)
                        Log.d("HomeFragment", ">> 비회원 $loginResponse")
                    }

                }else{
                    // 로그인 상태가 아닌 경우
                    Log.e("HomeFragment", "Failed to get home data: ${response.code()}")
                }
            }catch (e:Exception){
                // 오류
                Log.e("HomeFragment", "Error fetching user info: ${e.message}", e)
            }
        }

    }


    private fun updateUI(homeData: HomeService.LoginResponse?) {

        Log.d("HomeFragment", ">> $homeData")
        setupTrendingPostsRecyclerView()
        Log.d("HomeFragment", ">> trendingPosts")
        homeData?.trendingPosts?.let { trendingPosts ->
            trendingPostsAdapter.submitList(trendingPosts)
        }




        if (homeData != null ) {
            binding.userLayout.visibility=View.VISIBLE
            binding.unUserLayout.visibility=View.GONE

            binding.nonMembersLayout.visibility=View.GONE
            binding.membersRallyLayout.visibility=View.VISIBLE


            // 사용자 정보가 제대로 반환되었을 때만 UI 업데이트
            homeData.userInfo?.let { userInfo ->
                // 사용자 이미지
                Glide.with(this)
                    .load(userInfo.profileImageUrl.toString()) // 서버에 저장된 이미지 URI
                    .placeholder(R.drawable.signup_default_profile_image) // 이미지를 불러오는 동안 보여줄 임시 이미지
                    .error(R.drawable.signup_default_profile_image) // 이미지 로드 실패 시 보여줄 이미지
                    .into(binding.homeMemberProfileCiv) // 이미지를 설정할 ImageView

                // 사용자 아이콘
                Glide.with(this)
                    .load(userInfo.profileIconUrl.toString())
                    .placeholder(null)
                    .into(binding.homeMemberIconIv)

                // 사용자 뱃지
                Glide.with(this)
                    .load(userInfo.profileTitleUrl.toString())
                    .placeholder(null)
                    .into(binding.homeMemberIconIv)

                // 사용자 닉네임
                binding.homeMemberNameTv.text = userInfo.nickname


            }

            homeData.rally?.let { rally ->
                binding.homeRallyingTv.text=rally.name
                binding.rallyLocationdetailTotalTv.text=rally.pilgrimageNumber.toString()
                binding.rallyLocationdetailCheckTv.text=rally.completeNumber.toString()

                // 회원랠리화면
                Glide.with(this)
                    .load(rally.backgroundImageUrl.toString())
                    .placeholder(null)
                    .into(binding.homeRallyIv)
            }
        }
        else{
            // 비회원
            binding.userLayout.visibility = View.GONE
            binding.unUserLayout.visibility = View.VISIBLE
        }
    }

}




