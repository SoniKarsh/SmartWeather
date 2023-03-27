<p align="center">
  <a href="" rel="noopener">
    <a href="https://imgbb.com/"><img width=200px height=200px src="https://i.ibb.co/ZxgzsSz/ic-launcher.png" alt="ic-launcher" border="0"></a>
  </a>
</p>

<h1 align="center">SmartWeather</h1>
<p align="center"> Write an android app to authenticate user via sign in if already registered and if not then signup to login to our app
and check the current weather with next 5 days of forecast and details. Can also check weather of any other possible city by searching it through the
list of cities.
    <br> 
</p>

## 📝 Table of Contents
- [About](#about)
- [Components Used](#components)
- [Implementations](#implementations)

## About <a name = "about"></a>
This project has few simple yet an important features like authentication and using any third part library and showcasing its data.
Started with splashscreen api which is there to check whether the user is logged in or not and if yes then redirecting it to the user profile screen.
If not logged in then need to follow auth flow where a user either need to login or signup to access our app. It is developed using
MVVM architecture with single activity and multiple fragments. Used Koin to provide DI across this app. Once user logs in then can see
weather of a default city and can later manually add any city from the list to check the weather. Kept it as simple as possible and
minimalistic with not handling each and every case due to time constraints and also was not the demand of the project itself as it is a
demo to showcase skills though can agree that there are many areas where I could improve.

## Components Used <a name = "components"></a>
- MVVM Architecture
- Firebase Authentication
- View binding
- Flow, Coroutines
- Kotlin Koin for DI (Dependency Injection)
- Glide for image loading
- Retrofit - Api calling
- Splash screen API - To give generic UX for all the versions
- Navigation graph

## Implementations <a name = "implementations"></a>



