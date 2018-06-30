#!/usr/bin/python3
import random, hashlib, re

klucz_hardkodowany = 2137

def generowanie_klucza_prywatnego():
    print("Generuje klucz prywatny")
    klucz = [] #null
    i = 0
    while(i != 32):
        klucz.append(chr(random.randint(33, 126)))
        i += 1
    print("Wygenerowany klucz: " + ''.join(klucz) + "\n")
    return(''.join(klucz))

def generowanie_kilku_losowych_bajtow():
    print("Generuje losowe bajty")
    losowe = [] #null
    i = 0
    while(i != 4):
        if(random.randint(0,1) == 1):
            a = random.randint(48,57)
        else:
            a = random.randint(97, 102)
        losowe.append(chr(a))
        i += 1
    print("Wygenerowane losowe bajty: " + ''.join(losowe) + "\n")
    return(''.join(losowe))

# main
print("JESTEM URZĄDZENIEM 1")
klucz = generowanie_klucza_prywatnego()
losowe_bajty = generowanie_kilku_losowych_bajtow()

buffer_hashu = klucz + losowe_bajty
str(buffer_hashu)

obiekt = hashlib.md5()
obiekt.update(buffer_hashu.encode('utf-8'))
md5_z_klucza_prywatnego_i_hardkodowanego = obiekt.hexdigest()
print("MD5 z klucza prywatnego i hardkodowanego: " + md5_z_klucza_prywatnego_i_hardkodowanego)
dlugosc_md5_przed_dodanymi_bajtami = str(len(md5_z_klucza_prywatnego_i_hardkodowanego))
kopia_zapasowa_hashu = md5_z_klucza_prywatnego_i_hardkodowanego

print("Wrzucam losowo losowe bajty...")
md5_z_klucza_prywatnego_i_hardkodowanego = re.findall('.', md5_z_klucza_prywatnego_i_hardkodowanego)
i = 0
while(i != 4):
    md5_z_klucza_prywatnego_i_hardkodowanego.insert(random.randint(0,len(md5_z_klucza_prywatnego_i_hardkodowanego)), losowe_bajty[i])
    i += 1

md5_z_klucza_prywatnego_i_hardkodowanego = ''.join(md5_z_klucza_prywatnego_i_hardkodowanego)

print("MD5 z dodanymi losowymi bajtami: " + md5_z_klucza_prywatnego_i_hardkodowanego + "\n")
print("Długość MD5 z dodanymi bajtami: " + str(len(md5_z_klucza_prywatnego_i_hardkodowanego)))
print("Długość MD5 bez dodanych bajtów: " + dlugosc_md5_przed_dodanymi_bajtami + "\n")

print("JESTEM URZĄDZENIEM 2")

print("Mój klucz prywatny jest taki sam: " + klucz)
print("Więc mój md5 z klucza prywatnego i hardkodowanego jest taki sam: " + kopia_zapasowa_hashu + "\n")

print("Szukam różnic...")
i = 0
j = 0
k = []
while(i != 36):
    if(kopia_zapasowa_hashu[i-j] != md5_z_klucza_prywatnego_i_hardkodowanego[i]):
        k.append(md5_z_klucza_prywatnego_i_hardkodowanego[i])
        j += 1
    i += 1

print("Znalezione różnice to: " + ''.join(k) + " a dodane bajty wprowadzone w urządzeniu 1 to: " + losowe_bajty + "\n")
print("Sprawdzam czy dodane bajty się zgadzają...")

koncowe_bajty = ''.join(k)
koncowe_bajty = re.findall('.', koncowe_bajty) # dzielenie na tablice
losowe_bajty = re.findall('.', losowe_bajty) # dzielenie na tablice
i = 0
j = 0
while(i != 4):
    if(koncowe_bajty[i] in losowe_bajty):
        j += 1
    i += 1
if(j==4):
    print("Dodane bajty są takie same! Powodzenie!")
else:
    print("Dodane bajty się nie zgadzają! Rozłączam!")
