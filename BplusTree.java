import java.util.Scanner;
import java.util.Arrays;
import java.io.*;

//a global variable class used for assigning uniq node number for each node
class GlobalVariables{
    public static int node_no;

}

//BPnode which defines the btree node structure that has n keys and n+1 links
class BPnode{
    BPnode link[],elink;
    int [] key;
    int no;
    char nt;

    //constructor for BPnode which takes n_key as parameter and assigns all link to null and values to -999
    public BPnode(int n_key){
        int i;
        this.nt = 'e';
        this.no = GlobalVariables.node_no++;
        link = new BPnode[n_key+1];
        key = new int[n_key];
        for(i=0;i<n_key;i++){
            link[i] = null;
            key[i] = -999;
        }
        link[i] = null;
    }

    //returns the number of keys present in the node
    public int get_no_items(int n_key){
        int i;
        for(i=0;i<n_key && this.key[i] != -999 ; i++);
        return i;
    }

    //sorts the keys in the node
    public void sort_keys(){
        System.out.println("till here");
        Arrays.sort(this.key);
    }

    //iparent,nsert the item into the node
    public void insert_item_into_node(int item,int i){
        i -= 1;
        while(i!=-1 && item < key[i])
        {
            key[i+1] = key[i];
            i--;
        }
        key[++i] = item;
    }
    public int find_index_of_item(int item)
    {
        for(int i=0;i<key.length;i++)
            if(key[i] == item)
                return i;

        System.out.println("Not Possible");
        return -1;

    }
    public int find_index_of_item(int keys[], int item)
    {
        for(int i=0;i<keys.length;i++)
            if(keys[i] == item)
                return i;
        System.out.println("Not possible");
        return -1;
    }
    //which will shift links in the parent node after the middle key has been put into the node and make space for
    //refering new node
    public void shift_links(int index)
    {
        for(int i= get_no_items(key.length)-1;i>=index;i--)
            this.link[i+1] = this.link[i];
    }
    //
    public BPnode yet_to_be_named(int [] temp_keys,int n_key,int val)
    {
        int middle_key = (n_key+1)/2,i,j;
        BPnode s_node = new BPnode(n_key);

        j = middle_key+ val;
        for(i=0;i<n_key;i++){
            if(i<middle_key)
                this.key[i] = temp_keys[i];
            else
                this.key[i] = -999;
            if(j<=n_key) 
                s_node.key[i] = temp_keys[j++]; 
            else
                s_node.key[i] = -999;
        }
        if(val == 0){
            s_node.elink = this.elink;
            this.elink = s_node;
        }
        return s_node;

    }
    public boolean is_leaf_node(){
        if(link[0] == null)
            return true;
        return false;
    }

}


//BPT is a class that takes care of operations such as insertion, node traversal, Dotty Display
class BPT{
    int n_key;

    public BPT(int n_key){
        this.n_key = n_key;
    }

    //method which is used to find the parent of the requested node
    public BPnode find_parent(BPnode parent,BPnode child){
        int i;
        BPnode temp = parent,prev = null;
        if(parent == child)
            return child;
        while(temp != child)
        {
            for(i=0;i < n_key && temp.key[i] != -999 && temp.key[i] <= child.key[0] ; i++);
            prev = temp;
            temp = temp.link[i];
        }
        return prev;
    }

    public int[] combine_array(BPnode child,int item){

        int t[] = new int[child.key.length+1];
        t[child.key.length] = item;
        for(int i=0;i<child.key.length;i++){
            t[i] = child.key[i];
        }
        Arrays.sort(t);
        return t;
    }

    public BPnode split_node(BPnode parent,BPnode father, BPnode child, int item,int no_items_father){
        int temp_keys[] = combine_array(child,item),middle_key = (n_key+1)/2,i,j,ind,val=1;
        if(child.is_leaf_node())
            val = 0;
        if(child.is_leaf_node() && no_items_father < n_key){
            BPnode s_node = child.yet_to_be_named(temp_keys,n_key,val);
            if(no_items_father < n_key) {
                father.insert_item_into_node(temp_keys[middle_key],father.get_no_items(n_key)); 
                father.shift_links(((ind = father.find_index_of_item(temp_keys[middle_key]))+1));
                father.link[ind+1] = s_node;
                return parent;
            }    
        }
        else {


            if(no_items_father < n_key){
                father.insert_item_into_node(item,father.get_no_items(n_key));
                father.shift_links(((ind = father.find_index_of_item(item))+1));
                father.link[ind+1] = child;
                return parent;

            }
            else {
                int father_keys[] = combine_array(father,temp_keys[middle_key]);
                BPnode s_node;
                //System.out.println(child.link[0].is_leaf_node());
                if( !child.is_leaf_node() ){//&&  child.link[0].is_leaf_node()){ 
                    System.out.println(child.link[0].is_leaf_node());
                    father_keys = combine_array(father,item);
                    s_node = child;   //got a problem here      

                }
                else
                    s_node = child.yet_to_be_named(temp_keys,n_key,val);   //got a problem here      
                BPnode fs_node = father.yet_to_be_named(father_keys,n_key,1);
                int pos_middle_key = father.find_index_of_item(father_keys,temp_keys[middle_key]);
                if(pos_middle_key == middle_key){

                    System.out.println("step 1");//+pos_middle_key);
                    j = middle_key+1;
                    fs_node.link[0] = s_node;
                    for(i=1;j<=n_key;i++,j++){
                        fs_node.link[i] = father.link[j];
                        father.link[j] = null;
                    }
                }
                else
                    if(pos_middle_key < middle_key ){
                    System.out.println("step 2");
                        j = middle_key;
                        for(i=0;j<=n_key;i++,j++){
                            fs_node.link[i] = father.link[j];
                            father.link[j] = null;
                        }
                        father.shift_links(((ind = father.find_index_of_item(temp_keys[middle_key]))+1));
                        father.link[ind+1] = s_node;
                    //    System.out.println(ind+"ind"+father.link[2].key[0]+"father key"+fs_node.key[0]);
                        //yet to be completed
                    }
                    else{
                        System.out.println("step 3");
                        j = middle_key+1;
                        for(i=0;j<=n_key;j++,i++){
                            fs_node.link[i] = father.link[j];
                            father.link[j] = null;
                        }
                        fs_node.shift_links(((ind = fs_node.find_index_of_item(temp_keys[middle_key]))+1));
                        fs_node.link[ind+1] = s_node;
                    }
                BPnode parent_of_father = find_parent(parent,father);

                if(father == parent && no_items_father == n_key)
                {
                    System.out.println("hit");

                    BPnode god_father = new BPnode(n_key);
                    god_father.key[0] = father_keys[middle_key]; //??
                    god_father.link[0] = father;
                    god_father.link[1] = fs_node ;
                    return god_father;
                }   

                return split_node(parent,parent_of_father,fs_node,father_keys[middle_key],parent_of_father.get_no_items(n_key));

            }
        }
        System.out.println("It shud not come here");
        return parent;

    }

    //recursive method which traverse and inserts the item in correct place! splitting is done if necessary
    public BPnode insert(BPnode parent,BPnode root,int item){

        int n_items_in_rnode = root.get_no_items(n_key),i;
        BPnode father = find_parent(parent,root);
        int n_item_in_fnode = father.get_no_items(n_key);

        //if node is external/leaf node
        if(root.link[0] == null){


            //if external node is not full
            if(n_items_in_rnode < this.n_key){
                root.insert_item_into_node(item,n_items_in_rnode);
                return parent;
            }

            //if external node is full but it's parent is not @$$$%% will decide to do it generic later
            if(n_items_in_rnode == this.n_key ){
                return  split_node(parent,father,root,item,n_item_in_fnode);
            }
        }
        else {
            i=0;
            while(i!=n_key && root.key[i] != -999)
                if(root.key[i] > item)
                    return insert(parent,root.link[i],item);
                else
                    i+=1;
            return insert(parent,root.link[i],item);
        }
        System.out.println("IT should not come here");
        return parent;
    }

    public BPnode insert(BPnode parent,int item)
    {
        return insert(parent,parent,item);
    }


    //preorder traversal
    public void traversal(BPnode root){
        int i;
        for(i=0;i<n_key && (root.key[i] != -999); i++)
            System.out.println(root.key[i]+" "+root.no);
        for(i=0;i<n_key+1 && (root.link[i] != null ); i++)
            traversal(root.link[i]);
    }
    //preorder traversal and dumping it into the file "BTREE.dot"
    public void preorderDump(BPnode root,PrintWriter out) {
        //System.out.println("hi")
        out.print(root.no+" [label=\"");
        for(int i=0;i<n_key && root.key[i] != -999 ;i++)
            out.print(root.key[i]+" ");
        out.print("\"]\n");
        if(root.link[0] != null){
            for(int i=0;(i<n_key+1 && root.link[i] != null);i++)
                out.println(root.no+"->"+root.link[i].no+";");
            for(int i=0;i<=n_key && root.link[i] != null;i++)
                preorderDump(root.link[i],out);
        }

    }

    //method which creates a new file, and make it ready for dumping the tree
    public void display(BPnode root) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("BTREE.dot",true));
            out.println("digraph btree {\n");
            preorderDump(root,out);
            out.println("}\n");
            out.close();
            Runtime rt = Runtime.getRuntime();
            rt.exec("dot -Tps -O BTREE.dot");
            rt.exec("gnome-open BTREE.dot.ps &");
        }
        catch( Exception ex) {
            ex.printStackTrace();
        }
    }

}

public class BplusTree{
    public static void main(String[] args){
        BPT op = new BPT(3);
        BPnode root = new BPnode(3);
        BPnode parent;
        parent = root;
        BPnode n1 = new BPnode(3);
        BPnode n2 = new BPnode(3);
        BPnode n3 = new BPnode(3);
        BPnode n4 = new BPnode(3);
        BPnode n5 = new BPnode(3);
        BPnode n6 = new BPnode(3);
        root.nt = 'i';
        n1.nt = 'i';
        n2.nt = 'i';
        root.key[0] = 50;
        root.link[0] = n1;
        root.link[1] = n2;
        n1.key[0] = 25;
        n2.key[0] = 75;
        n1.link[0] = n3;
        n1.link[1] = n4;
        n2.link[0] = n5;
        n2.link[1] = n6;

        n3.key[0] = 12;
        n4.key[0] = 25;
        n4.key[1] = 37;
        n5.key[0] = 50;
        n5.key[1] = 60;
        n6.key[0] = 75;
        n6.key[1] = 100;
        n3.elink = n4;
        n4.elink = n5;
        n5.elink = n6;
        Scanner in = new Scanner(System.in); 
        int i=0,k;
        while(i<1000){
            k = in.nextInt();
            System.out.println("INput Value is"+ k);
            parent = op.insert(parent,k);
            i++;
        }
            op.display(parent);
    }
}

