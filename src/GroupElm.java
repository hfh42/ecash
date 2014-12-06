import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class GroupElm {
	
	private List<Integer> elements = new ArrayList<Integer>(); 

	public GroupElm(){
		init();
	}
	
	private void init(){				
		for(int i = 0; i < Group.q; i++){
			int g = Group.modPow(Group.g1,i);
			if (g == Group.g2) System.out.println("Found g2");
			int one = Group.modPow(g, Group.q);
			if (one != 1) System.out.println("g: " + g + ", g^q = " + one);
			elements.add(g);
		}
	}
	
	public List<Integer> getElements(){
		return elements;
	}
	
	public void printElements(){
		Collections.sort(elements);
		for(int i = 0; i < elements.size(); i += 10){
			for(int j = 0; j < 10; j++){
				if(i+j >= elements.size()) break;
				System.out.print(elements.get(i+j) + ", ");
			}
			System.out.println("");
		}
	}
	
	
}
