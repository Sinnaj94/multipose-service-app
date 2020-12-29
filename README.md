# Motion Capturing Client
This is the app that belongs to the server https://github.com/Sinnaj94/multipose-service-backend.
This app was programmed for Android.
## Dependencies
```
+--- com.otaliastudios:cameraview:2.6.4@aar
+--- org.jetbrains.kotlin:kotlin-android-extensions-runtime:1.4.0@jar
+--- androidx.navigation:navigation-fragment-ktx:2.3.0@aar
+--- androidx.fragment:fragment-ktx:1.2.4@aar
+--- androidx.navigation:navigation-ui-ktx:2.3.0@aar
+--- androidx.navigation:navigation-runtime-ktx:2.3.0@aar
+--- androidx.navigation:navigation-common-ktx:2.3.0@aar
+--- androidx.activity:activity-ktx:1.1.0@aar
+--- androidx.core:core-ktx:1.1.0@aar
+--- com.hluhovskyi.camerabutton:camerabutton-rxjava2-kotlin:2.0.1@aar
+--- com.otaliastudios.opengl:egloo:0.5.3@aar
+--- androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0@aar
+--- com.robertlevonyan.view:MaterialChipView:2.0.4@aar
+--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.71@jar
+--- androidx.collection:collection-ktx:1.1.0@jar
+--- androidx.lifecycle:lifecycle-livedata-core-ktx:2.2.0@aar
+--- androidx.lifecycle:lifecycle-runtime-ktx:2.2.0@aar
+--- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0@jar
+--- org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0@jar
+--- org.jetbrains.kotlin:kotlin-stdlib:1.4.0@jar
+--- androidx.navigation:navigation-ui:2.3.0@aar
+--- com.google.android.material:material:1.0.0@aar
+--- com.ramotion.paperonboarding:paper-onboarding:1.1.3@aar
+--- com.github.varunest:sparkbutton:1.0.6@aar
+--- com.github.jd-alexander:LikeButton:0.2.3@aar
+--- androidx.appcompat:appcompat:1.1.0@aar
+--- androidx.legacy:legacy-support-v4:1.0.0@aar
+--- androidx.constraintlayout:constraintlayout:1.1.3@aar
+--- androidx.navigation:navigation-fragment:2.3.0@aar
+--- androidx.lifecycle:lifecycle-extensions:2.2.0@aar
+--- com.github.bumptech.glide:glide:4.11.0@aar
+--- androidx.exifinterface:exifinterface:1.2.0@aar
+--- androidx.transition:transition:1.3.0@aar
+--- androidx.lifecycle:lifecycle-process:2.2.0@aar
+--- androidx.lifecycle:lifecycle-service:2.2.0@aar
+--- com.google.android.gms:play-services-tasks:17.2.0@aar
+--- com.google.android.gms:play-services-basement:17.4.0@aar
+--- androidx.fragment:fragment:1.2.4@aar
+--- androidx.navigation:navigation-runtime:2.3.0@aar
+--- androidx.activity:activity:1.1.0@aar
+--- androidx.appcompat:appcompat-resources:1.1.0@aar
+--- androidx.legacy:legacy-support-core-ui:1.0.0@aar
+--- androidx.legacy:legacy-support-core-utils:1.0.0@aar
+--- androidx.drawerlayout:drawerlayout:1.1.0@aar
+--- androidx.viewpager:viewpager:1.0.0@aar
+--- androidx.coordinatorlayout:coordinatorlayout:1.0.0@aar
+--- androidx.slidingpanelayout:slidingpanelayout:1.0.0@aar
+--- com.google.android.exoplayer:exoplayer:2.12.0@aar
+--- com.google.android.exoplayer:exoplayer-ui:2.12.0@aar
+--- androidx.recyclerview:recyclerview:1.1.0@aar
+--- androidx.customview:customview:1.1.0@aar
+--- androidx.vectordrawable:vectordrawable-animated:1.1.0@aar
+--- androidx.vectordrawable:vectordrawable:1.1.0@aar
+--- androidx.loader:loader:1.0.0@aar
+--- androidx.swiperefreshlayout:swiperefreshlayout:1.0.0@aar
+--- androidx.asynclayoutinflater:asynclayoutinflater:1.0.0@aar
+--- androidx.navigation:navigation-common:2.3.0@aar
+--- androidx.media:media:1.0.1@aar
+--- androidx.core:core:1.3.0@aar
+--- androidx.lifecycle:lifecycle-runtime:2.2.0@aar
+--- androidx.lifecycle:lifecycle-livedata:2.2.0@aar
+--- androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0@aar
+--- androidx.lifecycle:lifecycle-livedata-core:2.2.0@aar
+--- androidx.savedstate:savedstate:1.0.0@aar
+--- androidx.lifecycle:lifecycle-common:2.2.0@jar
+--- androidx.cursoradapter:cursoradapter:1.0.0@aar
+--- androidx.cardview:cardview:1.0.0@aar
+--- androidx.arch.core:core-runtime:2.1.0@aar
+--- androidx.arch.core:core-common:2.1.0@jar
+--- androidx.lifecycle:lifecycle-viewmodel:2.2.0@aar
+--- com.github.bumptech.glide:gifdecoder:4.11.0@aar
+--- com.google.android.exoplayer:exoplayer-dash:2.12.0@aar
+--- com.google.android.exoplayer:exoplayer-hls:2.12.0@aar
+--- com.google.android.exoplayer:exoplayer-smoothstreaming:2.12.0@aar
+--- com.google.android.exoplayer:exoplayer-core:2.12.0@aar
+--- androidx.documentfile:documentfile:1.0.0@aar
+--- androidx.localbroadcastmanager:localbroadcastmanager:1.0.0@aar
+--- androidx.print:print:1.0.0@aar
+--- androidx.interpolator:interpolator:1.0.0@aar
+--- androidx.versionedparcelable:versionedparcelable:1.1.0@aar
+--- androidx.collection:collection:1.1.0@jar
+--- com.hluhovskyi.camerabutton:camerabutton-rxjava2:2.0.1@aar
+--- com.hluhovskyi.camerabutton:camerabutton:2.0.1@aar
+--- com.google.android.exoplayer:exoplayer-extractor:2.12.0@aar
+--- com.google.android.exoplayer:exoplayer-common:2.12.0@aar
+--- androidx.annotation:annotation:1.1.0@jar
+--- com.github.medyo:fancybuttons:1.9.1@aar
+--- de.hdodenhof:circleimageview:3.1.0@aar
+--- com.squareup.retrofit2:converter-gson:2.9.0@jar
+--- com.squareup.retrofit2:retrofit:2.9.0@jar
+--- com.camerakit:camerakit:1.0.0-beta3.11@aar
+--- com.yqritc:android-scalablevideoview:1.0.4@aar
+--- com.camerakit:jpegkit:0.1.0@aar
+--- com.github.clans:fab:1.6.4@aar
+--- com.jackandphantom.android:androidlikebutton:1.2.0@aar
+--- com.squareup.okhttp3:logging-interceptor:3.8.0@jar
+--- org.jetbrains.kotlin:kotlin-stdlib-common:1.4.0@jar
+--- org.jetbrains:annotations:13.0@jar
+--- androidx.constraintlayout:constraintlayout-solver:1.1.3@jar
+--- com.squareup.okhttp3:okhttp:3.14.9@jar
+--- com.google.code.gson:gson:2.8.5@jar
+--- com.github.bumptech.glide:disklrucache:4.11.0@jar
+--- com.github.bumptech.glide:annotations:4.11.0@jar
+--- com.squareup.okio:okio:1.17.2@jar
+--- io.reactivex.rxjava2:rxandroid:2.0.1@aar
+--- io.reactivex.rxjava2:rxjava:2.1.9@jar
+--- com.google.guava:guava:27.1-android@jar
+--- org.reactivestreams:reactive-streams:1.0.2@jar
+--- com.google.guava:failureaccess:1.0.1@jar
\--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava@jar
``` 
