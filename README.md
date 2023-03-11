# myQuotes
**myQuotes** is an Android application where you can **Share**, **Discover** and **Save** New **Quotes!**

<a href='https://play.google.com/store/apps/details?id=com.sforge.quotes&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width="200" height="80" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

## Features
- About **1500** quotes for **You** to discover
- Endless Scroll like on **Youtube Shorts** or **Instagram Reels!**
- **Share** your **Favorite Quotes**
- **Quotes, Preferences and Collections** are saved in **[Firebase](https://firebase.google.com/)**
- **Request** and **View** all **Your Data** that is stored in **[Firebase](https://firebase.google.com/)**
- **Account Login, Logout and Username** Creation
- Ability to **Delete Your Account** with all data
- **Create** Your Own **Collections** and Add **Quotes** to them!
- **Search** All Quotes
- Set a **Background** for all **Quotes**

## What Data Is Being Stored? 
Your Data Is Stored in Two Places:
1. Your Account Info (Email and Password) gets Stored with [Firebase Auth](https://firebase.google.com/docs/auth)
2. Everything else gets Stored in [Firebase Realtime Database](https://firebase.google.com/docs/database)

In the Database, data is stored like this:
<details open><summary> root </summary><blockquote>

  <details><summary> Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
    <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Users </summary><blockquote>

  <details><summary> User1 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  
  bgId: rsz_forest_1

  bgQuality: low
  
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>

  
  username: username1
  

  </blockquote></details>

  <details><summary> User2 </summary><blockquote>

  <details><summary> Bookmarks </summary><blockquote>

  <details><summary> Collection1 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>

  </blockquote></details>
  <details><summary> Collection2 </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Preferences </summary><blockquote>
  
  <details><summary> Background </summary><blockquote>
  
  
  bgId: rsz_forest_1

  bgQuality: low
  
 
  </blockquote></details>
  </blockquote></details>

  <details><summary> User Quotes </summary><blockquote>

  <details><summary> Quote1 </summary><blockquote>

  
  author: Author1

  quote: Quote1

  user: User1
  
  </blockquote></details>
  <details><summary> Quote2 </summary><blockquote>

  
  author: Author2

  quote: Quote2

  user: User2
  
  </blockquote></details>
  </blockquote></details>

  
  username: username2
  

  </blockquote></details>
  </blockquote></details>
</blockquote></details>

## Licence

MIT License

Copyright (c) 2022 PuckyEU

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
