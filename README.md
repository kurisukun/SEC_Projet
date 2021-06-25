# SEC Projet: Chiffrement authentifié avec une clé YubiKey

## But

Le projet de départ consistait à faire du chiffrement authentifié avec Yubikey sur une clé USB de stockage. Suite à divers complication durant le design de notre architecture et afin de faire un projet de qualité dans le temps imparti, nous avons simplifié notre conception. Le chiffrement ne se fera plus sur un clé USB mais sur un fichier ou un dossier.

## Choix des algorithmes

### Chiffrement 

Un chiffrement avec Chacha20 a été envisagé mais la gestion des nonces pour chaque bloc nous as fait changé d'avis. Nous avons choisit une construction AES-CBC avec une clé de 256 bits et un padding PKCS5 (seul choix de padding possible).  

### KDF

Nous avons pris Argon2 avec un paramétrage de type sensible, car nous avons considéré que le temps de hachage pouvait prendre entre 1 et 2 secondes sans que cela pénalise l'expérience utilisateur. Nous avons mis la mémoire à 128 Mo,  et un degré de parallélisme à 4. 

### Fonctionnement 

Nous commençons par vérifier l'utilisateur à l'aide de sa yubikey. Si c'est bon nous allons vérifié la difficulté de son mot de passe afin d'avoir une bonne base pour le hachage avec argon2. 

Une fois cela fait, nous allons compressé le fichier ou le dossier que l'on veut traiter. Par la suite, nous allons lire par chunk de bytes, chiffrer et écrire le résultat dans un fichier annexe afin de garder une intégrité des données si le chiffrement venait à être interrompu. Le même processus est utilisé pour le déchiffrement.

## Problème rencontré

Nous avons rencontré le problème suivant en voulant chiffrer une clé USB. Si nous voulons l'ouvrir comme fichier ("/dev/sdax"), nous devons prendre en compte que aes-cbc rajoute 16 octets de padding, ces derniers ne pourrons pas être écrit sur la clé. On peut éventuellement choisir l’option sans padding, mais cela implique que la clé doit être formatée avec une taille qui est multiple de 16. 
