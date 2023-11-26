# Compilador

## To Do List

- Crear scopes para variables
- Eliminar DeclList de Program y Scope y reemplazar por SymbolTable
- Agregar campo scope para Symbol (Saber la altura de cada variable)
- Implementar las decl en stmtList

## Como ejecutar

Se tiene que tener java jdk instalado (yo tengo la versión de openJDK 21)
<a href="https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1+12/OpenJDK21U-jdk_x64_windows_hotspot_21.0.1_12.msi">Descargar</a>
Hay dos maneras
Abrir el proyecto con VS Code y utilizar <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>B</kbd> para acceder a los comandos de VSCode y ejecutar <span style="color:red; background-color:lightgrey; padding:3px 5px;">run-main</span>
Esto compilará y ejecutará automáticamente.

También, para ejecutar se pueden utilizar los siguientes comandos:

<b>Compilar JFlex</b>

```console
java -jar ./lib/jflex-full-1.9.1.jar -d ./src ./AnalizadorLexico.jflex
```

<b>Compilar JCup</b>

```console
java -jar ./lib/java-cup-11b.jar -destdir ./src -parser Parser -symbols sym -interface -expect 1 ./sintax.cup
```

<b>Ejecutar Código</b>

```console
java -cp '.\bin;.\lib\java-cup-11b-runtime.jar;.\lib\java-cup-11b.jar' App ./input.txt
```
