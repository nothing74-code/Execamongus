cmake_minimum_required(VERSION 3.18.1)
project("executor")

set(CMAKE_CXX_STANDARD 17)

add_library(
    executor
    SHARED
    main.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    executor
    ${log-lib}
)

