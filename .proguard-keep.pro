# Keep rules for rest-arch library.
# RestService is package-private and abstract; it is designed to be subclassed
# by library consumers at runtime and therefore must not be reported as dead code.
-keep abstract class com.services.RestService
-keepclassmembers class com.services.RestService {
    *;
}

# Keep ObjectNotFoundException for consumers catching it by type.
-keep class com.services.ObjectNotFoundException
