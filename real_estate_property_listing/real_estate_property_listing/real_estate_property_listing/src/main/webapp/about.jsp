<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>About Us | Dream Home Realty</title>
    <style>
        :root {
            --primary-blue: #2563eb;
            --light-blue: #3b82f6;
            --lighter-blue: #93c5fd;
            --white: #ffffff;
            --gray: #f3f4f6;
            --dark-gray: #6b7280;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: var(--gray);
            color: #333;
            line-height: 1.6;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        header {
            background-color: var(--primary-blue);
            color: var(--white);
            padding: 20px 0;
            text-align: center;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        h1 {
            margin: 0;
            font-size: 2.5rem;
        }

        h2 {
            color: var(--primary-blue);
            margin-top: 40px;
            border-bottom: 2px solid var(--lighter-blue);
            padding-bottom: 10px;
        }

        .about-hero {
            background: linear-gradient(rgba(37, 99, 235, 0.8), rgba(37, 99, 235, 0.8)),
            url('https://images.unsplash.com/photo-1560518883-ce09059eeffa?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80');
            background-size: cover;
            background-position: center;
            color: white;
            padding: 80px 20px;
            text-align: center;
            border-radius: 8px;
            margin-bottom: 40px;
        }

        .about-hero h2 {
            color: white;
            border-bottom: none;
            font-size: 2.2rem;
        }

        .about-content {
            background-color: var(--white);
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-bottom: 40px;
        }

        .team-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }

        .team-member {
            background-color: var(--white);
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;
        }

        .team-member:hover {
            transform: translateY(-5px);
        }

        .team-member img {
            width: 100%;
            height: 200px;
            object-fit: cover;
        }

        .team-member-info {
            padding: 20px;
        }

        .team-member h3 {
            color: var(--primary-blue);
            margin: 0 0 5px 0;
        }

        .team-member p {
            color: var(--dark-gray);
            margin: 5px 0;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin: 40px 0;
        }

        .stat-item {
            text-align: center;
            padding: 20px;
            background-color: var(--white);
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .stat-number {
            font-size: 2.5rem;
            font-weight: bold;
            color: var(--primary-blue);
            margin: 10px 0;
        }

        .back-btn {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 25px;
            background-color: var(--primary-blue);
            color: var(--white);
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
            font-weight: bold;
        }

        .back-btn:hover {
            background-color: var(--light-blue);
        }

        footer {
            background-color: var(--primary-blue);
            color: var(--white);
            text-align: center;
            padding: 20px 0;
            margin-top: 50px;
        }
    </style>
</head>
<body>
<header>
    <div class="container">
        <h1>Real Estate</h1>
    </div>
</header>

<div class="container">
    <section class="about-hero">
        <h2>Your Trusted Partner in Real Estate</h2>
        <p>With over 15 years of experience helping families find their dream homes</p>
    </section>

    <section class="about-content">
        <h2>Our Story</h2>
        <p>Founded in 2008, Dream Home Realty began as a small family business with a passion for connecting people with their perfect homes. What started as a local real estate agency has grown into one of the most trusted names in the industry, serving clients across the region.</p>

        <p>Our mission is simple: to make the home buying and selling process as smooth and stress-free as possible. We believe everyone deserves to find a place they can call home, and we're committed to making that dream a reality.</p>

        <div class="stats">
            <div class="stat-item">
                <div class="stat-number">15+</div>
                <p>Years of Experience</p>
            </div>
            <div class="stat-item">
                <div class="stat-number">2,500+</div>
                <p>Happy Clients</p>
            </div>
            <div class="stat-item">
                <div class="stat-number">$1.2B</div>
                <p>Properties Sold</p>
            </div>
            <div class="stat-item">
                <div class="stat-number">98%</div>
                <p>Client Satisfaction</p>
            </div>
        </div>
    </section>

    <section class="about-content">
        <h2>Our Team</h2>
        <p>Our success comes from our dedicated team of real estate professionals who bring expertise, integrity, and personalized service to every transaction.</p>

        <div class="team-grid">
            <div class="team-member">
                <img src="https://images.unsplash.com/photo-1560250097-0b93528c311a?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80" alt="John Doe">
                <div class="team-member-info">
                    <h3>John Doe</h3>
                    <p>Founder & CEO</p>
                    <p>15 years experience</p>
                </div>
            </div>
            <div class="team-member">
                <img src="https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80" alt="Jane Smith">
                <div class="team-member-info">
                    <h3>Jane Smith</h3>
                    <p>Senior Agent</p>
                    <p>12 years experience</p>
                </div>
            </div>
            <div class="team-member">
                <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80" alt="Michael Johnson">
                <div class="team-member-info">
                    <h3>Michael Johnson</h3>
                    <p>Property Specialist</p>
                    <p>8 years experience</p>
                </div>
            </div>
            <div class="team-member">
                <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80" alt="Sarah Williams">
                <div class="team-member-info">
                    <h3>Sarah Williams</h3>
                    <p>Client Relations</p>
                    <p>10 years experience</p>
                </div>
            </div>
        </div>
    </section>

    <section class="about-content">
        <h2>Our Values</h2>
        <p><strong>Integrity:</strong> We believe in honest, transparent communication and always putting our clients' needs first.</p>
        <p><strong>Expertise:</strong> Our team stays at the forefront of market trends and real estate knowledge.</p>
        <p><strong>Community:</strong> We're proud to be part of the neighborhoods we serve and actively give back.</p>
        <p><strong>Innovation:</strong> We leverage the latest technology to make your real estate journey seamless.</p>

        <a href="index.jsp" class="back-btn">Back to Home</a>
    </section>
</div>

<footer>
    <div class="container">
        <p>&copy; 2023 Dream Home Realty. All rights reserved.</p>
    </div>
</footer>
</body>
</html>