package algorithm.bitree;

public class AvlTree<T> {
    private Node<T> root = null;

    public static class Node<T> {
        int key;
        T data;
        Node<T> parent;
        int dept;
        int balance;

        Node<T> left;
        Node<T> right;

        public Node(int key, T data, Node<T> parent) {
            this.key = key;
            this.data = data;
            this.balance = 0;

            if(parent == null) {
                this.dept = 1;
            } else {
                this.parent = parent;
                this.dept = parent.dept + 1;
            }
        }

        public String toString() {
            return key + " L(" + (left == null ? "null" : left.key) + "), R(" + (right == null ? "null" : right.key) + ")";
        }
    }

    /**
     * 插入数据
     * @param data 数据
     * @return 旧值或null
     */
    public T insert(T data) {
        int key = key(data);
        if(root == null) {
            root = new Node<>(key, data, null);
            root.key = key;
            root.data = data;
            return null;
        }

        return insert(root, key, data);
    }

    private T insert(Node<T> parent, int key, T data) {
        if(parent.key > key) {
            if(parent.left == null) {
                parent.left = new Node<>(key, data, parent);
                // 插入前父节点没有子节点， 需判断数是否失衡
                if(parent.right == null) {
                    rebuild(parent.parent);
                }

                // 插入前父节点已经有子节点， 不需要做其他变化
                return null;
            }
            return insert(parent.left, key, data);
        } else if(parent.key < key) {
            if(parent.right == null) {
                parent.right = new Node<>(key, data, parent);
                // 插入前父节点没有子节点， 需判断数是否失衡
                if(parent.left == null) {
                    rebuild(parent.parent);
                }

                // 插入前父节点已经有子节点， 不需要做其他变化
                return null;
            }
            return insert(parent.right, key, data);
        } else {
            T oldData = parent.data;
            // key重复， 只更新值
            parent.data = data;
            return oldData;
        }
    }

    private void rebuild(Node<T> parent) {
        if(parent == null) {
            return;
        }

        int balance = calcBalance(parent);
        if(balance < -1) {  // 右 > 左
            Node<T> right = parent.right;
            if(right.right == null) {
                if(right.left != null) {
                    rightRotation(right);
                }
            } else {
                if(right.right.left != null) {
                    rightRotation(right.right);
                }
            }
            // 左旋
            leftRotation(parent);
        } else if(balance > 1) {    // 左 > 右
            Node<T> left = parent.left;
            if(left.left == null) {
                if(left.right != null) {
                    leftRotation(left);
                }
            } else {
                if(left.left.right != null) {
                    rightRotation(left.left);
                }
            }
            // 右旋
            rightRotation(parent);
        }

        rebuild(parent.parent);
    }

    private void leftRotation(Node<T> parent) {
        Node<T> parentParent = parent.parent;

        // 右节点是新的父节点
        Node<T> newParent = parent.right;
        newParent.parent = parentParent;
        if(parentParent == null) {
            // 说明是根节点
            root = newParent;
        } else {
            // 更改parent.parent的子节点
            if(parentParent.left == parent) {
                parentParent.left = newParent;
            } else if(parentParent.right == parent) {
                parentParent.right = newParent;
            }
        }

        // 原父节点的右节点成了newParent, newParent的左节点挂到原父节点的右侧
        parent.right = newParent.left;
        if(parent.right != null) {
            parent.right.parent = parent;
        }

        // 原父节点变成左节点
        newParent.left = parent;
        newParent.left.parent = newParent;

        // 重算子节点深度
        setChildDept(newParent);
        setChildDept(parent);
    }

    /**
     * 右旋
     * @param parent 父节点
     */
    private void rightRotation(Node<T> parent) {
        Node<T> parentParent = parent.parent;

        // 左节点是新的父节点
        Node<T> newParent = parent.left;
        newParent.parent = parentParent;
        if(parentParent == null) {
            // 说明是根节点
            root = newParent;
        } else {
            // 更改parent.parent的子节点
            if(parentParent.left == parent) {
                parentParent.left = newParent;
            } else if(parentParent.right == parent) {
                parentParent.right = newParent;
            }
        }

        // 原父节点的左节点成了newParent, newParent的右节点挂到原父节点的左侧
        parent.left = newParent.right;
        if(parent.left != null) {
            parent.left.parent = parent;
        }

        // 原父节点变成右节点
        newParent.right = parent;
        newParent.right.parent = newParent;


        // 重算子节点深度
        setChildDept(newParent);
        setChildDept(parent);
    }

    /**
     * 重算子节点的深度
     * @param parent 父节点
     */
    private void setChildDept(Node<T> parent) {
        parent.dept = parent.parent == null ? 1 : parent.parent.dept + 1;

        if(parent.left != null) {
            setChildDept(parent.left);
        }

        if(parent.right != null) {
            setChildDept(parent.right);
        }
    }

    /**
     * 计算以parent为根的树的平衡度
     * @param parent 根
     * @return -2 -1 0 1 2
     */
    private int calcBalance(Node<T> parent) {
        int leftDept = parent.left == null ? parent.dept : getMaxDept(parent.left);
        int rightDept = parent.right == null ? parent.dept : getMaxDept(parent.right);
        return leftDept - rightDept;
    }

    private int getMaxDept(Node<T> parent) {
        return Math.max(parent.left == null ? parent.dept : getMaxDept(parent.left), parent.right == null ? parent.dept : getMaxDept(parent.right));
    }

    public int key(T data) {
        return data == null ? 0 : data.hashCode();
    }

    public static void main(String[] args) {
        AvlTree<Object> avlTree = new AvlTree<>();
        avlTree.insert(100);
        avlTree.insert(50);
        avlTree.insert(150);
        avlTree.insert(200);
        // 左旋
        avlTree.insert(250);
        System.out.println(avlTree.root);

        // 先右旋、再左旋
        avlTree.insert(225);
        System.out.println(avlTree.root);


        // 先右旋、再左旋
        avlTree.insert(235);
        System.out.println(avlTree.root);
    }
}
