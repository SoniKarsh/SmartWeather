package com.technobugsai.smartweather.modules.activity

import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {
    viewModel {
        AuthViewModel(
            get()
        )
    }

//    single {
//        providePhotoRemoteDS(get())
//    }

}

//fun providePhotoRemoteDS(photosApi: FitPeoPhotosApi): PhotosRemoteDataSource {
//    return PhotosRemoteDataSourceImpl(photosApi)
//}
