cmake_minimum_required(VERSION 3.4.1)
include_directories(C:/Users/ismke/Belgeler/lanedetectionopencv/include)

add_library(lib_opencv SHARED IMPORTED)
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION C:/Users/ismke/Belgeler/lanedetectionopencv/android/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java4.so)

add_library(my_functions SHARED C:/Users/ismke/Belgeler/lanedetectionopencv/opencv/opencv.cpp)
find_library(log-lib log)
target_link_libraries(my_functions lib_opencv ${log-lib})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fvisibility=default")
