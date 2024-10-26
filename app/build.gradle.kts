plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.google.gms.google.services)
}

android {
	namespace = "com.example.voidchat"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.voidchat"
		minSdk = 24
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures.viewBinding = true
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	implementation(libs.firebase.auth)
	implementation(libs.firebase.database)
	implementation(libs.firebase.storage)
	implementation(libs.firebase.messaging)
	implementation(libs.firebase.inappmessaging.display)
	implementation(libs.glide)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)

	// Jetpack Compose integration
	implementation(libs.androidx.navigation.compose)

	// Views/Fragments integration
	implementation(libs.androidx.navigation.fragment)
	implementation(libs.androidx.navigation.ui)

	// Feature module support for Fragments
	implementation(libs.androidx.navigation.dynamic.features.fragment)

	//Circle Image
	implementation(libs.circleimageview)

	//Dexter
	implementation (libs.dexter)



	//image picker
	implementation(libs.imagepicker)

	implementation(libs.androidx.activity.ktx)
	implementation(libs.androidx.fragment.ktx)

	implementation(libs.circleimageview)

	implementation(libs.coil.compose)

}