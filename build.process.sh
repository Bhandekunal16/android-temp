compiler='./gradlew'

$compiler clean &&

$compiler build --stacktrace &&

$compiler assembleDebug &&

$compiler installDebug 