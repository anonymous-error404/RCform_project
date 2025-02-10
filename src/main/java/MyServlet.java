import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.xdevapi.JsonArray;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MyServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        HttpSession ses = req.getSession(true);
        PrintWriter pw = res.getWriter(); // Get PrintWriter from ServletResponse
        JSONObject jsonResponse = new JSONObject();
        JSONArray array = new JSONArray();
        Login l = new Login();

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        try{
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String action = req.getParameter("action1");
        System.out.println("action is "+action);

        if ("verification".equals(action)) {

        RailwayConcession rc = new RailwayConcession();

        int student_id = Integer.parseInt(req.getParameter("id"));
        String student_name = req.getParameter("name");
        LocalDate student_birthdate = LocalDate.parse(req.getParameter("birthdate"), formatter);
        String address = req.getParameter("address");
        String phone = req.getParameter("phone number");
        String department = req.getParameter("dept");
        String year = req.getParameter("year");

        ArrayList<Object> formData = new ArrayList<Object>();
        formData.add(student_id);
        formData.add(student_name);
        formData.add(student_birthdate);
        formData.add(address);
        formData.add(phone);
        formData.add(department);
        formData.add(year);
        System.out.println(formData);

        boolean vf = rc.Verification(student_id, student_name, student_birthdate);

        if(vf)
        {
            ses.setAttribute("formData",formData);
            jsonResponse.put("verified", "yes");
            res.getWriter().write(jsonResponse.toString());
            System.out.println("yes");
        }
        else
        {
            jsonResponse.put("verified", "no");
            res.getWriter().write(jsonResponse.toString());
            System.out.println("no");
        }
    }

        else if("signUp".equals(action)){

        String register_username = req.getParameter("username");
        String register_password = req.getParameter("pswd");

        System.out.println(ses.getAttribute("formData"));
        String response =  l.register(register_username,register_password,(ArrayList)ses.getAttribute("formData"));
        System.out.println(response);

        if(response.equals("registered")){
        
            System.out.println("Registeration Done Successfully");
            jsonResponse.put("registered", "done");
            res.getWriter().write(jsonResponse.toString());
        }
        else if(response.equals("account exists")){

        jsonResponse.put("registered", "account exists");
        res.getWriter().write(jsonResponse.toString());

        }
        else if(response.equals("username exists")){
            jsonResponse.put("registered", "username exists");
            res.getWriter().write(jsonResponse.toString());
        }
        else{
            jsonResponse.put("registered", "Error");
            res.getWriter().write(jsonResponse.toString());
        }
        
    }
    else if("Login".equals(action)){

        String login_username = req.getParameter("username");
        String login_password = req.getParameter("pswd");
        ArrayList<String> logged_in_user = new ArrayList<String>();
        logged_in_user.add(login_username);
        logged_in_user.add(login_password);

        // Assuming Login class and login method return user data in ArrayList<Object>
        ArrayList<Object> login_info = l.login(login_username, login_password);
    
        // Check if the login is successful
        if (logged_in_user != null && !login_info.isEmpty()) {
            // Store user data in session
            ses.setAttribute("login_info", logged_in_user);
    
            System.out.println("Logged in");

            jsonResponse.put("account", "found");
            res.getWriter().write(jsonResponse.toString());

        } else {
            jsonResponse.put("account", "not found");
            res.getWriter().write(jsonResponse.toString());
        }
    }
    else if(action.equals("page2") || action.equals("fillform") || action.equals("history")){

        ArrayList<String> login_infObjects = (ArrayList)ses.getAttribute("login_info");
        ArrayList<Object> userData = l.login(login_infObjects.get(0), login_infObjects.get(1));
        ses.setAttribute("userData", userData);

        jsonResponse.put("username", userData.get(0));
        jsonResponse.put("name", userData.get(1));
        jsonResponse.put("id", userData.get(2));
        System.out.println(userData.get(3));

        if(action.equals("page2")){

        if(userData.get(3).equals("no existing form")){
            jsonResponse.put("form", "can fill");
        }
        else if(userData.get(3).equals("form exists")){
            jsonResponse.put("form", "can edit");
            jsonResponse.put("conID", userData.get(8));
        }
        else if(userData.get(3).equals("Already has one issued")){
            jsonResponse.put("form", "no need");
        }
    }

        if(action.equals("fillform")){

            if(userData.get(3).equals("form exists")){

                jsonResponse.put("formtype", "edit");
                jsonResponse.put("duration", userData.get(4));
                jsonResponse.put("class", userData.get(5));
                jsonResponse.put("dept", userData.get(6));
                jsonResponse.put("dest", userData.get(7));

            }
            else if(userData.get(3).equals("no existing form")){

                jsonResponse.put("formtype", "fill");
            }
        }

        if(action.equals("history")){

            RailwayConcession rc = new RailwayConcession();
            ArrayList<Object> past_forms = rc.historyChecker((Integer)userData.get(2));

            if(past_forms == null)
            jsonResponse.put("history", "error");
            else if(past_forms.get(0).equals("no history found"))
            jsonResponse.put("history", "not found");
            else{
                jsonResponse.put("history", "found");

                for(int i = 0; i < past_forms.size(); i++){

                    array.put(past_forms.get(i));

                }
                jsonResponse.put("history_data", array);
            }

        }

        res.getWriter().write(jsonResponse.toString());

    }
    else if("Concession_form_fill".equals(action)){

            RailwayConcession rc = new RailwayConcession();

            String cls = req.getParameter("class");
            System.out.println(cls);
            String dur = req.getParameter("duration");
            System.out.println(dur);
            String dep = req.getParameter("departure");
            System.out.println(dep);
            String des = req.getParameter("destination");
            System.out.println(des);
            ArrayList<Object> forID = (ArrayList)ses.getAttribute("userData");

            String cid = rc.getConcessionInfo((Integer)forID.get(2), cls, dur, dep, des);
            System.out.println(cid);

            if(cid != null){
            jsonResponse.put("Concession_id", cid);
            res.getWriter().write(jsonResponse.toString());
            }
            else{
            jsonResponse.put("Concession_id", "Error");
            res.getWriter().write(jsonResponse.toString());
            }
        }
        else if("Concession_form_edit".equals(action)){

            RailwayConcession rc = new RailwayConcession();
            String new_class = req.getParameter("class");
            String new_duration = req.getParameter("duration");
            String new_dept = req.getParameter("departure");
            String new_dest = req.getParameter("destination");
            ArrayList<Object> userData = (ArrayList)ses.getAttribute("userData");

            boolean ok = rc.editForm((Integer)userData.get(2),new_class,new_duration,new_dept,new_dest);

            if(ok){
                System.out.println("Form Updated Successfully");
                jsonResponse.put("update", "successful");
            }
            else{
                System.out.println("Error Updating form");
                jsonResponse.put("update", "unsuccessful");
            }
            res.getWriter().write(jsonResponse.toString());

        }
        else if(action.equals("user_account")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            userData = l.getuseraccount((String)userData.get(0),(String)userData.get(1), (Integer)userData.get(2));

            jsonResponse.put("uname", userData.get(0));
            jsonResponse.put("sid", userData.get(1));
            jsonResponse.put("sname", userData.get(2));
            jsonResponse.put("bdate", userData.get(3));
            jsonResponse.put("add", userData.get(4));
            jsonResponse.put("phno", userData.get(5));
            jsonResponse.put("dept", userData.get(6));
            jsonResponse.put("yr", userData.get(7));
            
            res.getWriter().write(jsonResponse.toString());
        }
        else if(action.equals("change_uname")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            String new_username = req.getParameter("edited_username");
            System.out.println(new_username);
            l.changeUsername((String)userData.get(0),new_username);

        }
        else if(action.equals("change_add")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            String new_add = req.getParameter("edited_add");
            l.changeAdd((String)userData.get(0),new_add);
            
        }
        else if(action.equals("change_phno")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            String new_phno = req.getParameter("edited_phno");
            l.changePhno((String)userData.get(0),new_phno);
            
        }
        else if(action.equals("change_dept")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            String new_dept = req.getParameter("edited_dept");
            l.changeDept((String)userData.get(0),new_dept);
            
        }
        else if(action.equals("change_yr")){

            ArrayList userData = (ArrayList)ses.getAttribute("userData");
            String new_yr = req.getParameter("edited_yr");
            l.changeYear((String)userData.get(0),new_yr);
            
        }
    }
        catch(Exception e){
            e.printStackTrace();
        }

        pw.close();

        }
    }