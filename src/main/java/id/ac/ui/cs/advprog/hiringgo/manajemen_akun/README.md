
# CRUD Manajemen Akun

|Versi|Pengarang|Tanggal|
|:---|:---|:---|
|1.0|Christian Raphael Heryanto|6/14/2025|

----

## Create Mahasiswa API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/admin/accounts/mahasiswa`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|
|Content-Type|`application/json`|

#### Request Body Sample
```json
{
  "email": "newmahasiswa@example.com",
  "password": "12345678",
  "fullName": "newMahasiswa",
  "nim": "2106754322"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|M|Email akun mahasiswa|
|password|String|M|Sandi akun mahasiswa yang ingin digunakan|
|fullName|String|M|Nama lengkap mahasiswa|
|nim|String|M|Nomor Induk Mahasiwa|


### __API Response__

|Tipe|_Value_|
|:---|:---|
|HTTP Status|201|


#### Response Body Sample
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Mahasiswa account created successfully",
  "data": {
    "id": "24d56638-a05c-421b-9847-34aaa44d4319",
    "email": "newmahasiswa@example.com",
    "role": "MAHASISWA",
    "fullName": "newMahasiswa",
    "nim": "2106754322",
    "nip": null
  },
  "timestamp": "2025-06-14T13:59:06.53304056"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|User|M|_Object_ User yang dibuat|
|timestamp|String|M|Waktu _response_|

## Create Dosen API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/admin/accounts/dosen`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|
|Content-Type|`application/json`|

#### Request Body Sample
```json
{
  "email": "newdosen@example.com",
  "password": "12345678",
  "fullName": "newDosen",
  "nip": "2106754323"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|M|Email akun dosen|
|password|String|M|Sandi akun dosen yang ingin digunakan|
|fullName|String|M|Nama lengkap dosen|
|nip|String|M|Nomor Induk Pegawai|


### __API Response__

|Tipe|_Value_|
|:---|:---|
|HTTP Status|201|


#### Response Body Sample
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Dosen account created successfully",
  "data": {
    "id": "51fb56c6-f787-4026-8b94-973dfce84d93",
    "email": "newdosen@example.com",
    "role": "DOSEN",
    "fullName": "newDosen",
    "nim": null,
    "nip": "2106754323"
  },
  "timestamp": "2025-06-14T14:16:42.203378958"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|User|M|_Object_ User yang dibuat|
|timestamp|String|M|Waktu _response_|

## Create Admin API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/admin/accounts/admin`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|
|Content-Type|`application/json`|

#### Request Body Sample
```json
{
  "email": "newadmin@example.com",
  "password": "12345678"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|M|Email akun admin|
|password|String|M|Sandi akun admin yang ingin digunakan|


### __API Response__

|Tipe|_Value_|
|:---|:---|
|HTTP Status|201|


#### Response Body Sample
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Admin account created successfully",
  "data": {
    "id": "8bef3140-32d8-4cfb-af32-e8d039d38b42",
    "email": "newadmin@example.com",
    "role": "ADMIN",
    "fullName": null,
    "nim": null,
    "nip": null
  },
  "timestamp": "2025-06-14T14:18:39.075892786"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|User|M|_Object_ User yang dibuat|
|timestamp|String|M|Waktu _response_|

## Read All User API

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/admin/accounts`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|

### __API Response__

|Tipe|__Value__|
|:---|:---|
|HTTP Status|200|

#### Request Response Sample
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": "c24641a2-d618-446e-829a-de30db6db1a2",
      "email": "admin@hiringgo.com",
      "role": "ADMIN",
      "fullName": null,
      "nim": null,
      "nip": null
    },
    {
      "id": "9e072b1e-5ccb-4cb2-ae53-578348f2e008",
      "email": "dosen@example.com",
      "role": "DOSEN",
      "fullName": "dosen",
      "nim": null,
      "nip": "2306214990"
    },
    {
      "id": "fb621d60-cbf4-4f5c-ac8b-de5816757b2b",
      "email": "jimmy.neutron@example.com",
      "role": "MAHASISWA",
      "fullName": "Jimmy Neutron",
      "nim": "1234567890",
      "nip": null
    }
  ],
  "timestamp": "2025-06-14T13:44:40.146050989"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|Array of User|M|List dari semua User yang terdapat dalam database|
|timestamp|String|M|Waktu _response_|

## Read User by ID

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| POST |
|Endpoint|`/api/admin/accounts/{id}`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|

#### Request Parameter
|_Key_|_Value_|
|:---|:---|
|id|`<pk:uuid>`|

#### Request Parameter Sample
Endpoint: `api/admin/accounts/fb621d60-cbf4-4f5c-ac8b-de5816757b2b

### __API Response__

|Tipe|__Value__|
|:---|:---|
|HTTP Status|200|

### Response Body Sample
```json
{
  "success": true,
  "statusCode": 200,
  "message": "User retrieved successfully",
  "data": {
    "id": "fb621d60-cbf4-4f5c-ac8b-de5816757b2b",
    "email": "jimmy.neutron@example.com",
    "role": "MAHASISWA",
    "fullName": "Jimmy Neutron",
    "nim": "1234567890",
    "nip": null
  },
  "timestamp": "2025-06-14T13:49:43.843477943"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|User|M|_Object_ User yang memiliki id sesuai dengan parameter|
|timestamp|String|M|Waktu _response_|

## Update User By ID

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| PUT |
|Endpoint|`/api/admin/accounts/{id}`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|
|Content-Type|`application/json`|

#### Request Parameter
|_Key_|_Value_|
|:---|:---|
|id|`<pk:uuid>`|

#### Request Parameter Sample
Endpoint: `api/admin/accounts/24d56638-a05c-421b-9847-34aaa44d4319

#### Request Body Sample
```json
{
  "email": "mahasiswa2dosen@example.com",
  "password": "87654321",
  "fullName": "mahasiswa2dosen",
  "identifier": "33333333",
  "newRole": "DOSEN"
}
```

#### Penjalasan Request Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|email|String|O|Email akun baru|
|password|String|O|Sandi akun mahasiswa yang ingin digunakan|
|fullName|String|O|Nama lengkap mahasiswa|
|identifier|String|O, C|nim atau nip, tergantung value newRole|
|newRole|String|O|"MAHASISWA", "DOSEN", "ADMIN"|


### __API Response__

|Tipe|_Value_|
|:---|:---|
|HTTP Status|200|


#### Response Body Sample
```json
{
  "success": true,
  "statusCode": 200,
  "message": "User updated successfully",
  "data": {
    "id": "24d56638-a05c-421b-9847-34aaa44d4319",
    "email": "mahasiswa2dosen@example.com",
    "role": "DOSEN",
    "fullName": "mahasiswa2dosen",
    "nim": null,
    "nip": "33333333"
  },
  "timestamp": "2025-06-14T14:29:27.624447669"
}
```

#### Penjelasan Response Body
|Parameter|Tipe Data|M, O, C|Deskripsi|
|:---|:---|:---|:---|
|success|boolean|M|Indikator keberhasilan _request_|
|statusCode|int|M|HTTP Status Code|
|message|String|M|Informasi _response_|
|data|User|M|_Object_ User yang diperbarui|
|timestamp|String|M|Waktu _response_|

## Delete User by ID

### __API Request__

|Tipe| _Value_ |
| :--- | :---    |
|HTTP Method| DELETE |
|Endpoint|`/api/admin/accounts/{id}`|

#### Request Headers
|_Key_|_Value_|
|:---|:---|
|Authorization|Bearer `<token>`|

#### Request Parameter
|_Key_|_Value_|
|:---|:---|
|id|`<pk:uuid>`|

#### Request Parameter Sample
Endpoint: `api/admin/accounts/24d56638-a05c-421b-9847-34aaa44d4319


### __API Response__

|Tipe|_Value_|
|:---|:---|
|HTTP Status|204|


