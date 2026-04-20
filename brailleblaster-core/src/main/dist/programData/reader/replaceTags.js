/**
Keep brl tags inside html tags in xhtml file which I get from brailleblaster translator library 
Modified BFS algorithm to traverse dom elements
@version :0.0.0
**/
var rootTree = 'body'
i = 0;
var childrenTree;
/* create an empty queue */
var queue = [];
queue.push(rootTree);


while (queue.length != 0) {
	//change rootTree to the top of queue
	rootTree = queue.shift();
	// get all direct children of rootTree
	childrenTree = $(rootTree).contents().get();
	for (j in childrenTree) {
		/* if the tag is not brl or img tag*/
		if (!($(childrenTree[j]).is("brl , img"))) {

			// find if any of its children and its decent has brl or img
			if ($(childrenTree[j]).find('brl , img').length == 0) {
				$(childrenTree[j]).remove();
			}
			// if it has brl in its decent, add that node to queue
			else {
				queue.push(childrenTree[j]);

			}
		}

	}

	i = i + 1;
}
/**
Change style by Jquery
replace current style with ToBraille.css stylesheet
**/
var mylink=$('head link')[0];
mylink.setAttribute('href','ToBraille.css');


