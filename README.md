# JConvertDecompiler

JConvertDecompiler est un projet que j'ai crée en 2017-2018 dans un projet de reverse-engineering d'un logiciel.

Il m'a été nécessaire d'avoir un logiciel qui permettait de décompiler (binaire -> code (plus ou moins bien retraduit)) du code binaire
en code Java (d'où le JConvertDecompiler) plus ou moins correspondant au code initial selon certaines règles.
En effet il était question de décompiler plusieurs fichiers dans des répertoires spécifiques en gardant la même arborescence d'où mon idée
de directement refaire un logiciel. En comptant en plus que le cadre du reverse-engineering nécessitait des paramètres de décompilation
spécifiques au projet et tant de détails ne laissait la possibilité qu'à faire un outil propre à soi-même.

Le projet est assez simple, composé d'une fenêtre il demande le repértoire de fichiers à décompiler et le répertoire où décompiler les fichiers.
La décompilation se fait telle quelle : java bytecode -> code java

Le décompilateur utilisé est procyon version 0.5.30.
