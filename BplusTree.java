import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;
import java.io.*;

//a global variable class used for assigning uniq node number for each node
class GlobalVariables{
    public static int node_no,no_keys;

}

//BPnode which defines the btree node structure that has n keys and n+1 links
class BPnode{
    BPnode link[],elink;
    int [] key;
    int no;
    char nt;
    Lock lock = new Lock();

    //constructor for BPnode which takes n_key as parameter and assigns all link to null and values to -999
    public BPnode(int n_key){
        int i;
        this.nt = 'e';
        System.out.println(GlobalVariables.node_no);
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
    public void copy_vl(BPnode temp,int n_key){
        int no = temp.get_no_items(n_key);
        for(int i=0;i<no;i++){
            this.link[i]= temp.link[i];
            this.key[i] = temp.key[i];
            temp.key[i] = -999;
            temp.link[i] = null;
        }
        this.link[no] = temp.link[no];
        temp.link[no] = null;
        return;
    }


    //something is goin wrong here
    public BPnode yet_to_be_named(int [] temp_keys,int n_key,int val)
    {
        int middle_key = (n_key+1)/2,i,j;
        BPnode s_node = new BPnode(n_key);
        for(int k=0;k<=n_key;k++)
            System.out.println(temp_keys[k]);

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
                if(father.is_leaf_node())
                    father_keys = temp_keys;
                BPnode s_node;
                if( !child.is_leaf_node() ){//&&  child.link[0].is_leaf_node()){ 
                    System.out.println(child.link[0].is_leaf_node());
                    father_keys = combine_array(father,item);
                    s_node = child;         
                }
                else
                    s_node = child.yet_to_be_named(temp_keys,n_key,val);   //got a problem here      
                BPnode fs_node = father.yet_to_be_named(father_keys,n_key,1);
                fs_node.elink = father.elink;
                father.elink = fs_node;
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
                    BPnode god_father = new BPnode(n_key); // rename god_father to split child;
                    if(parent.is_leaf_node())
                        fs_node = s_node;

                    god_father.copy_vl(father,n_key);
                    father.key[0] = father_keys[middle_key]; //??
                    father.link[0] = god_father;
                    father.link[1] = fs_node ;
                    god_father.elink = fs_node;

                    return father;
                }   
                return split_node(parent,parent_of_father,fs_node,father_keys[middle_key],parent_of_father.get_no_items(n_key));
                }
            }
            System.out.println("It shud not come here");
            return parent;

        }

        //recursive method which traverse and inserts the item in correct place! splitting is done if necessary
        public synchronized BPnode insert(BPnode parent,BPnode root,int item){

            int n_items_in_rnode = root.get_no_items(n_key),i;
            BPnode father = find_parent(parent,root);
            int n_item_in_fnode = father.get_no_items(n_key);

            //if node is external/leaf node
            if(root.is_leaf_node()){//root.link[0] == null){

                //*****  Call move right if necessary and make sure that it should be unblocked once insertion is successful
                //root.move_right();
                //if external node is not full
                
                if(n_items_in_rnode < this.n_key){
                    try{
                    root.lock.lock();
                    root.insert_item_into_node(item,n_items_in_rnode);
                    } catch (Exception e) {;  }
                    root.lock.unlock();
                    return parent;
                }

                //if external node is full 
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
                out.print(root.no+" [label=\"");
                for(int i=0;i<n_key && root.key[i] != -999 ;i++)
                    out.print(root.key[i]+" ");
                out.print("\"]\n");
                //           for(BPnode temp = root; temp.elink!=null ;temp= temp.elink)
                //                  out.println(temp.no+"->"+temp.elink.no+";");
                if(root.link[0] != null){
                    for(int i=0;(i<n_key+1 && root.link[i] != null);i++)
                        out.println(root.no+"->"+root.link[i].no+";");
                    for(int i=0;i<=n_key && root.link[i] != null;i++)
                        preorderDump(root.link[i],out);
                }


            }
            public void exterlinkDump(BPnode root,PrintWriter out) {
                for(BPnode temp = root.link[0];temp!=null;temp = temp.link[0]){
                    for(BPnode t = temp; t.elink!=null;t = t.elink)
                        out.println(t.no+"->"+t.elink.no+";");
                    out.print(" {rank = same; ");
                    for(BPnode t = temp; t!=null;t = t.elink)
                        out.print(t.no+";");
                    out.print("}");
                }
            }

            //method which creates a new file, and make it ready for dumping the tree
            public void display(BPnode root) {
                try {
                    PrintWriter out = new PrintWriter(new FileWriter("BTREE.dot",true));
                    out.println("digraph btree {\n");
                    preorderDump(root,out);
                    exterlinkDump(root,out);
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

        class ThreadClass implements Runnable {
            BPnode parent;
            BPT op;
            Thread t;
            Random randomG = new Random();
            Scanner in = new Scanner(System.in);

            ThreadClass(BPnode parent,BPT tar){
                op = tar;
                this.parent = parent;
                t = new Thread(this);
                t.start();
            }

            public void run(){
                //generate random number and insert
              try {
               for(int i=0;i<5;i++) {
   //               t.sleep(1000);
                  parent = op.insert(parent,in.nextInt());
               
               }
               op.display(parent);
    ;
              } catch (Exception e){ ; }
                ;
            }
        }

        class Lock{
            private boolean isLocked = false;

            public synchronized void lock() throws InterruptedException {
                
                while(isLocked) {
                    wait();
                }
                isLocked = true;
            }
            public synchronized void unlock() {
                isLocked = false;
                notify();
            }
        }

        class BplusTree{
            public static void main(String[] args){
                Scanner in = new Scanner(System.in); 
                GlobalVariables.no_keys = in.nextInt();
                BPT op = new BPT(GlobalVariables.no_keys);

                BPnode parent = new BPnode(GlobalVariables.no_keys);
                ThreadClass th1 = new ThreadClass(parent,op);

                int i=in.nextInt(),k;
                while(i>0){
                    try {
 //                   Thread.sleep(1000);
                    k = in.nextInt();
                    System.out.println("INput Value is"+ k);
                    parent = op.insert(parent,k);
                    i--;
                    } catch (Exception e){ ;} 
                }
                op.display(parent);
                try {
                }catch (Exception e) { ;}
            }
        }

