package assign1;

import java.util.Comparator;

public class HuffmanComperator implements Comparator<HuffmanNode> {

	@Override
	public int compare(HuffmanNode arg0, HuffmanNode arg1) {
		int res = arg0.get_freq() - arg1.get_freq();
		if (res != 0)
			return res;
		return ((int)arg0.get_char() - (int)arg1.get_char());
	}


}
