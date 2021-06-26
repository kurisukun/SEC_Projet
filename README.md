# Ango: Chiffrement authentifié avec une clé YubiKey



**Auteurs:** Arn Jérôme, Barros Henriques Chris



## But

Le projet de départ consistait à faire du chiffrement authentifié avec Yubikey sur une clé USB de stockage. Suite à diverses complications durant le design de notre architecture et afin de faire un projet de qualité dans le temps imparti, nous avons simplifié notre conception. Le chiffrement ne se fera plus sur un clé USB mais sur un fichier ou un dossier.

## Choix des algorithmes

### Chiffrement 

Un chiffrement avec Chacha20 a été envisagé mais la gestion des nonces pour chaque bloc nous as fait changé d'avis. Nous avons choisi une construction AES-CBC avec une clé de 256 bits et un padding PKCS5 (seul choix de padding possible).  

### KDF

Nous avons pris Argon2 avec un paramétrage de type sensible, car nous avons considéré que le temps de hachage pouvait prendre entre 1 et 2 secondes sans que cela pénalise l'expérience utilisateur. Nous avons mis la mémoire à 128 Mo,  et un degré de parallélisme à 4. 

### Fonctionnement 

Nous commençons par vérifier l'utilisateur à l'aide de sa yubikey. Si c'est bon nous allons vérifié la difficulté de son mot de passe afin d'avoir une bonne base pour le hachage avec argon2. 

Une fois cela fait, nous allons compressé le fichier ou le dossier que l'on veut traiter. Par la suite, nous allons lire par chunk de bytes, chiffrer et écrire le résultat dans un fichier annexe afin de garder une intégrité des données si le chiffrement venait à être interrompu. Le même processus est utilisé pour le déchiffrement.

## Problème rencontré

Nous avons rencontré le problème suivant en voulant chiffrer une clé USB. Si nous voulons l'ouvrir comme fichier (**/dev/sdax**), nous devons prendre en compte qu'AES-CBC rajoute 16 octets de padding, ces derniers ne pourrons pas être écrits sur la clé. On peut éventuellement choisir l’option sans padding, mais cela implique que la clé doit être formatée avec une taille qui est multiple de 16. 



## Utilisation

Pour produire un fichier jar exécutable depuis le projet, il suffit de lancer:

```bash
mvn clean install
```

Puis dans le dossier **target**, le fichier **ango-jar-with-dependencies.jar** a été produit. Dès lors, on peut le lancer à l'aide de Java:



```
❯ java -jar ango-jar-with-dependencies.jar

Usage: ango [-deh] [--confFile=<configFile>] [--dstPath=<dstPathName>]
            [-p=<password>] [--srcPath=<srcPathName>]
try to encrypt and decrypt your usb key
      --confFile=<configFile>
                   Config file for decryption
  -d               Decrypt file
      --dstPath=<dstPathName>
                   Destination of result
  -e               Encrypt file
  -h, --help       display this help and exit
  -p=<password>    Passord to encrypt or decrypt file
      --srcPath=<srcPathName>
                   file to encrypt or decrypt
```



Une autre manière d'afficher l'aide est de lui passer l'option `--help` ou `-h`.



### Chiffrement

Imaginons que l'on souhaite chiffrer un fichier nommé **test**  qui contient le texte "test" et qui se trouve dans le même dossier que notre exécutable. 

```


```

On voit ici que l'on passe à Java le nom du jar **ango**, que l'on passe l'option `-e`  pour effectuer un chiffrement au moyen d'un mot de passe passé en paramètre avec `-p`. 

`--srcPath`  et `--dstPath` servent à respectivement définir le chemin du fichier à chiffrer et le chemin vers lequel le ciphertext va être créé. Il n'y a pas besoin de préciser que ce que l'on souhaite chiffrer est un dossier ou un fichier simple, Ango le reconnaît directement et agit en conséquence en chiffrant le contenu du dossier en gardant sa hiérarchie.

`--confFile` pour indiquer le nom du fichier de configuration qui doit être utilisé pour inscrire l'IV et le sel utilisé pour Argon2.



Avant de pouvoir effectuer un chiffrement, il faut être authentifié en tant qu'utilisateur auprès de l'API YubiKey, il est donc demandé d'entrer un one-time password. 

```
To use ango, you have to authenticate
Please put your finger on your YubiKey to enter your One-time password:
```

Si le mot de passe entré ne correspond pas à un mot de passe valide ou que son format est incorrect, une erreur s'affiche et annule l'opération de chiffrement. 



Lorsque le chiffrement est terminé, on obtient le fichier **ciphertext** dont on peut afficher le contenu:

```
❯ cat ciphertext 
@�����q�E~�o��(iQp4KgReq���/U���
?|,v�Q"X���L�v.4�ɡ��U���R�6q��$�`��'@ý���%JG�u/\�p���p@�@
                                                         ��٩{7�+6�iF�P2��+S��FXh3O�/���t��W����% 
```





### Déchiffrement

Pour déchiffrer le fichier précédemment généré, on utilise la commande suivante en précisant le même mot de passe que précédemment:

```
java -jar ango -d -p=CryptoIsRigolo_1234$ --srcPath="ciphertext" --dstPath="decrypted" --confFile="confFile"
```

 

On peut vérifier que notre fichier **test** existe bien dans le dossier **decrypted** et contient le texte "test": 

```
❯ ls decrypted && cat decrypted/test                                                              
test
test
```





### Mauvaise utilisation

À chaque fois que l'on entre une option qui n'est pas définie, l'application affiche une erreur. Par exemple:

```
❯ java -jar ango-jar-with-dependencies.jar -a

[ERROR] message : Unknown option: '-a' 
Usage: ango [-deh] [--confFile=<configFile>] [--dstPath=<dstPathName>]
            [-p=<password>] [--srcPath=<srcPathName>]
try to encrypt and decrypt your usb key
      --confFile=<configFile>
                   Config file for decryption
  -d               Decrypt file
      --dstPath=<dstPathName>
                   Destination of result
  -e               Encrypt file
  -h, --help       display this help and exit
  -p=<password>    Passord to encrypt or decrypt file
      --srcPath=<srcPathName>
                   file to encrypt or decrypt
```
