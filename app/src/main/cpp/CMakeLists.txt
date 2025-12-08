cmake_minimum_required(VERSION 3.18.1)

project("jni-helper")

# Add debug symbols in a safe way - use -g2 for better symbol information
# -g2 includes more debugging information than -g (line numbers, local variables)
# -O3 enables maximum optimization for release builds while keeping debug symbols for Crashlytics
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -g2 -O3")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -g2 -O3")

set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -g2")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -g2")

# Ensure symbols are not stripped
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--build-id")

add_library(jni-helper SHARED
            jni-helper.cpp)

target_link_libraries(jni-helper)
