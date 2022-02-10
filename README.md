# Object-relational-Mapper
Requirements:<br/>
<ol>
<li>Class hierarchy mapping between class -> table </li>
<li>ORM loader to create class instances </li>
<li>DB interface (preferably CRUD)  </li>
<li>Class hierarchy to encapsulate db criteria </li>
<li>Implemented MariaDB and SQLite support </li>
</ol>
ORMLoader Functions:<br/>
<ul>
<li><b>Get</b> values from table and instantiate corresponding objects</li>
<li><b>Update</b> a value already read from the table by setting the new values on the instantiated object</li>
<li><b>Insert</b> an object manually created into the database</li>
<li><b>Delete</b> values from the database based on criteria or on an already read object</li>
<li><b>Create Table</b> creates the table based on the given class</li>
<li><b>Drop Table</b> removes the table based on the given class</li>
</ul>
<br/>

Criteria Functions:<br/>
<ul>
<li><b>LT</b> lower than</li>
<li><b>GT</b> greater than</li>
<li><b>EQ</b> equals</li>
<li><b>LIKE</b> uses a standard sql pattern for matching</li>
<li><b>Use the object primary keys</b></li>
</ul>