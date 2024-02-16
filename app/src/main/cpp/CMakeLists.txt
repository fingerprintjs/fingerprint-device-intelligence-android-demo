cmake_minimum_required(VERSION 3.18.1)

project("jni-helper")

add_library(jni-helper SHARED
            jni-helper.cpp)

target_link_libraries(jni-helper)
