# SEC Projet: Chiffrement authentifié avec une clé YubiKey

## But

Le projet de départ consistait à faire du chiffrement authentifié avec Yubikey sur une clé USB de stockage. Suite à divers complication durant le design de notre architecture et afin de faire un projet de qualité dans le temps imparti, nous avons simplifié notre conception. Le chiffrement ne se fera plus sur un clé USB mais sur un fichier ou un dossier.

## Choix des algorithmes

### Chiffrement 

Un chiffrement avec Chacha20 a été envisagé mais la gestion des nonces pour chaque bloc nous as fait changé d'avis. Nous avons choisit une construction AES-CBC avec une clé de 256 bits et un padding PKCS5 (seul choix de padding possible).  

### KDF

Nous avons pris Argon2 avec un paramétrage de type sensible, car nous avons considéré que le temps de hachage pouvait prendre entre 1 et 2 secondes sans que cela pénalise l'expérience utilisateur. Nous avons mis la mémoire à 128 Mo,  et un degré de parallélisme à 4. 

### Fonctionnement 

## Problème rencontré

