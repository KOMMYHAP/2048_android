project(Wusiko2048Server)

add_subdirectory(libraries)

add_executable(${PROJECT_NAME} main.cpp)
target_link_libraries(${PROJECT_NAME} PRIVATE basiclib)
