Testing simple funcionality, zero or one element on matcher tree, element can be one of the three possibilities
The tree to match is an one node tree or with zero elements
Also testing when matcher receives an empty tree
The requirements are different but simple

Output
req1 -> tree1 --> true
req1 -> tree2 --> false
req1 -> tree3 --> false

req2 -> tree1 --> false
req2 -> tree2 --> false
req2 -> tree3 --> false

req3 -> tree1 --> true
req3 -> tree2 --> false
req3 -> tree3 --> false

req4 -> tree1 --> true
req4 -> tree2 --> false
req4 -> tree3 --> true
