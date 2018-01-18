// $Id: AddUser.js,v 1.3 2016-08-30 09:48:35-04 ericholp Exp $

function validate() {
    try {
        var oneinvalid = 0;

        var patt1 = /^\s*$/;
        var patt2 = /^.{1,60}$/;
        if (patt1.test($("#username").val()) || !patt2.test($("#username").val())) {
            oneinvalid = 1;
            $("#usernameerr").show();
            $("#username").css("border", "solid 2px red");
        } else {
            $("#usernameerr").hide();
            $("#username").css("border", "");
        }

        var patt = /^[A-Za-z]+$/;
        if (!patt.test($("#firstname").val())) {
            oneinvalid = 1;
            $("#firstnameerr").show();
            $("#firstname").css("border", "solid 2px red");
        } else {
            $("#firstnameerr").hide();
            $("#firstname").css("border", "");
        }

        var patt = /^[A-Za-z]+$/;
        if (!patt.test($("#lastname").val())) {
            oneinvalid = 1;
            $("#lastnameerr").show();
            $("#lastname").css("border", "solid 2px red");
        } else {
            $("#lastnameerr").hide();
            $("#lastname").css("border", "");
        }

        var empatt1 = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
        var empatt2 = /^\"[A-Za-z\s\&]+\"\s*\<[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\>$/;
        if (!empatt1.test($("#email").val()) && !empatt2.test($("#email").val())) {
            oneinvalid = 1;
            $("#emailerr").show();
            $("#email").css("border", "solid 2px red");
        } else {
            $("#emailerr").hide();
            $("#email").css("border", "");
        }

        if (oneinvalid) {
            $("#alert_box_top").show();
            $("#alert_box_bottom").show();
        } else {
            $("#form").submit();
        }
    } catch (ex) {
        alert("validate: " + ex);
    }
}

function add(parent, child_tag) {
    return parent.appendChild(document.createElement(child_tag));
}

function add_department(select) {

    var department_id = select.value;
    var department_name = select.options.item(select.selectedIndex).text;
    select.selectedIndex = 0;

    var block = document.getElementById('department');
    var department = document.createElement('div');
    block.insertBefore(department, select.parentNode);

    var hidden = add(department, 'input');
    hidden.name = 'department_id';
    hidden.type = 'hidden';
    hidden.value = department_id;

    var label_span = add(department, 'span');
    label_span.className = 'label';
    var name_span = add(label_span, 'span');
    name_span.name = 'department_name';
    name_span.textContent = department_name;

    var checkbox_span = add(department, 'span');
    checkbox_span.className = 'checkbox label';

    var label = add(checkbox_span, 'label');
    label.textContent = ' \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0 Active?';


    var checkbox = add(checkbox_span, 'input');
    checkbox.name = 'department_active';
    checkbox.type = 'checkbox';
    checkbox.value = department_id;
    checkbox.checked = true;

    var button_span = add(department, 'span');
    button_span.className = 'button';
    var button = add(button_span, 'input');
    button.id = 'remove_department_button';
    button.type = 'button';
    button.value = '-';
    button.setAttribute('onclick', 'remove_field( this )');

    var counter = document.getElementById('department_counter');
    counter.value++;
}

function remove_field(button) {
    var department = button.parentNode.parentNode;
    department.parentNode.removeChild(department);

    var counter = document.getElementById('department_counter');
    counter.value--;
}
