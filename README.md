# Proyecto Final de Teoría de la Computación - Simulador de AF, AP, GR, GLC y MAQUINAS DE TURING

Este proyecto consiste en el desarrollo de un entorno gráfico interactivo diseñado para facilitar el aprendizaje y la comprensión de los principales modelos computacionales estudiados en Teoría de la Computación. El simulador permite visualizar, construir y ejecutar Autómatas Finitos Deterministas (AFD), Autómatas de Pila (AP), Gramáticas Regulares (GR), Gramáticas Libres de Contexto (GLC) y Máquinas de Turing (MT).

# Objetivo del proyecto:
Desarrollar una plataforma interactiva que permita comprender, construir y simular los modelos fundamentales de la Teoría de la Computación, fortaleciendo el aprendizaje mediante visualización y práctica guiada.
# [MANUAL DE USUARIO] (https://docs.google.com/document/d/10dUA0QkagRIdiicMRPBsvKZ1ZOBnbEHPBPp2R2ZNrak/edit?usp=sharing)
# 1. Modo AFD (Autómata Finito Determinista)
Permite definir completamente un autómata ingresando:
Alfabeto de entrada
Conjunto de estados
Estado inicial
Estados finales (de aceptación)
Tabla de transiciones
Una vez configurado, el sistema analiza cualquier palabra ingresada por el usuario, determinando si es aceptada o rechazada por el autómata, mostrando el proceso de evaluación paso a paso.

# 2. Modo Gramática Regular
Posibilita la definición de una gramática regular mediante producciones en formato estándar (ejemplo: A → aB | b). A partir de esta gramática, el programa puede:
Verificar si una cadena específica pertenece al lenguaje generado
Realizar esta comprobación mediante conversión interna a un autómata finito equivalente para su análisis

# 3. Modo Gramática Libre de Contexto (GLC)
Soporta la definición de gramáticas independientes del contexto con producciones como S → aSb | ε. Para una cadena ingresada, el programa ofrece:
Visualización de derivaciones (por la izquierda o por la derecha)
Generación del árbol sintáctico correspondiente
(Funcionalidad opcional) Transformación de la gramática a Forma Normal de Chomsky o Forma Normal de Greibach

# 4. Modo Autómata de Pila (AP)
Implementa un simulador completo de autómata de pila que incluye:
Configuración de todos los componentes formales (estados, alfabetos, transiciones, etc.)
Ejecución paso a paso sobre cadenas de entrada
Visualización en tiempo real del contenido y evolución de la pila durante el procesamiento

# 5. Modo Máquina de Turing (MT) (funcionalidad extendida opcional)
Simula una máquina de Turing completa, permitiendo:
Definición de estados, alfabetos de entrada y de cinta, tabla de transiciones y estados de aceptación
Ejecución detallada mostrando los cambios en la cinta en cada paso del cálculo
Ejemplos prácticos como reconocimiento de palíndromos o operaciones aritméticas con números binarios

# DESARROLLADO POR:
- Yesenia Michelle Chuc Kuyoc
- Edwin Roberto Cauich Aguilar
