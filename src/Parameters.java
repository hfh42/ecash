
public class Parameters {
	public static final int p = 2027; //1073742623;
	public static final int q = 1013; //536871311; // p=2q+1 , p = 1073742623 
	public static final int g1 = 506; //48395724; // g1^{p-1/2} = -1 mod p
	public static final int g2 = 800; //455432218;
}

/*
	q = 536871311
	p = 2q+1 = 1073742623
	
	p-1/2 = 536871311
	
	-1 mod p = 1073742622
	
	g1 = 48395724
	
	g1^(p-1/2) = -1 mod p
	
	x = 393
	g2 = g1^x mod p = 455432218
*/

/*
	q = 1013
	p = 2q+1 = 2027

	-1 mod p = 2026

	g1 = 506
	
	x = 119
	g2 = 800

*/