uthentication

|Versi|Pengarang|Tanggal|
|:---|:---|:---|
|1.0|Christian Raphael Heryanto|6/14/2025|

----

## Register API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/auth/register`|


#### Request Body Sample
```json
{
  "email": "test@example.com",
  "password": "12345678",
  "confirmPassword": "12345678",
  "fullName": "Test User",
  "nim": "2106754321"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|M|Email akun mahasiswa|
|password|String|M|Sandi akun mahasiswa yang ingin digunakan|
|confirmPassword|String|M|Konfirmasi sandi yang ingin digunakan|
|fullName|String|M|Nama lengkap mahasiswa|
|nim|String|M|Nomor Induk Mahasiwa|


### API Response

|Tipe|_Value_|
|:---|:---|
|HTTP Status|200|


#### Response Body Sample
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiTUFIQVNJU1dBIiwibmltIjoiMjEwNjc1NDMyMSIsInRva2VuVmVyc2lvbiI6MCwiZnVsbE5hbWUiOiJUZXN0IFVzZXIiLCJpZCI6IjA0MTZhODY1LWZiODYtNDY0Mi04Zjk3LWEzOWZjMjEzNjhhYiIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDk5MDQ5NTQsImV4cCI6MTc0OTkwODU1NH0.Xn5Qa8FrqqjC1iQPWxE8uYFlYWpD8kyjnms_NS29HJY",
  "expiresIn": 3600000,
  "user": {
    "role": "MAHASISWA",
    "nim": "2106754321",
    "tokenVersion": 0,
    "fullName": "Test User",
    "id": "0416a865-fb86-4642-8f97-a39fc21368ab",
    "email": "test@example.com"
  }
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|token|String|M|JWT token yang menyimpan data user yang terbuat|
|expiresIn|int|M|Durasi token valid dalam _milliseconds_|
|user|User|M|_Object_ pengguna yang terbuat, memiliki atribut dan nilai yang sama dengan yang diberi saat _request_|

## Login API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/auth/login`|

#### Request Body Sample
```json
{
  "email": "test@example.com",
  "password": "12345678"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|M|Email akun yang ingin digunakan|
|password|String|M|Sandi akun pengguna yang ingin digunakan|

### API Response

|Tipe|_Value_|
|:---|:---|
|HTTP Status|200|


#### Response Body Sample
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiTUFIQVNJU1dBIiwibmltIjoiMjEwNjc1NDMyMSIsInRva2VuVmVyc2lvbiI6MCwiZnVsbE5hbWUiOiJUZXN0IFVzZXIiLCJpZCI6IjA0MTZhODY1LWZiODYtNDY0Mi04Zjk3LWEzOWZjMjEzNjhhYiIsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDk5MDQ5NTQsImV4cCI6MTc0OTkwODU1NH0.Xn5Qa8FrqqjC1iQPWxE8uYFlYWpD8kyjnms_NS29HJY",
  "expiresIn": 3600000,
  "user": {
    "role": "MAHASISWA",
    "nim": "2106754321",
    "tokenVersion": 0,
    "fullName": "Test User",
    "id": "0416a865-fb86-4642-8f97-a39fc21368ab",
    "email": "test@example.com"
  }
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|token|String|M|JWT token yang menyimpan data user yang terbuat|
|expiresIn|int|M|Durasi token valid dalam _milliseconds_|
|user|User|M|_Object_ pengguna yang terbuat, memiliki atribut dan nilai yang sama dengan yang diberi saat _request_|

