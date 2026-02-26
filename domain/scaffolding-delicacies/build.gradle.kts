plugins {
    alias(libs.plugins.seraphim.android.library)
}
android {
    namespace = "com.seraphim.domain.scaffolding.delicacies"
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}