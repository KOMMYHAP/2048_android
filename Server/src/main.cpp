#include <iostream>

int main()
{
	const uint32_t max_tries = 42;
	uint32_t tries = 0;
	int i = 0;
	do
	{
		i = omp_get_max_threads();
		
	#pragma omp parallel
		{
			--i;
		}
	}
	while (tries++ < max_tries && i != 0);
	std::cout << "Race detected on " << tries << " try: OpenMP is correctly working." << std::endl;
}
